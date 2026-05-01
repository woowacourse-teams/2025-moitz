package com.f12.moitz.application;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.SelectedCandidates;
import com.f12.moitz.domain.FinalCandidateSelector;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Routes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecommendationPlaceSearchService {

    private static final int PLACE_SEARCH_BATCH_SIZE = 5;

    private final PlaceRecommender placeRecommender;
    private final FinalCandidateSelector finalCandidateSelector = new FinalCandidateSelector();

    public RecommendationPlaceSearchService(
            @Qualifier("placeRecommenderParallelAdapter") final PlaceRecommender placeRecommender
    ) {
        this.placeRecommender = placeRecommender;
    }

    public RecommendationPlaceSearchResult search(
            final CandidateSelection candidateSelection,
            final List<RecommendCondition> recommendConditions,
            final Map<Place, Routes> candidateRoutes,
            final int searchLimit,
            final int targetCount
    ) {
        final Map<Place, CategorizedRecommendedPlaces> accumulatedRecommendedPlaces = new LinkedHashMap<>();
        final List<Place> searchedPlaces = new ArrayList<>();
        SelectedCandidates selectedCandidates = selectFinalPlaces(
                candidateSelection,
                searchedPlaces,
                accumulatedRecommendedPlaces,
                recommendConditions,
                targetCount
        );

        while (searchedPlaces.size() < searchLimit) {
            if (selectedCandidates.getSelectedPlaces().size() >= targetCount) {
                log.debug("장소 추천 조기 종료 - 최종 후보 {}개 확보", targetCount);
                break;
            }

            final int batchLimit = Math.min(
                    PLACE_SEARCH_BATCH_SIZE,
                    searchLimit - searchedPlaces.size()
            );
            final List<Place> batch = finalCandidateSelector.selectNextSearchPlaces(
                    candidateSelection,
                    searchedPlaces,
                    place -> satisfiesPlaceRequirements(place, accumulatedRecommendedPlaces, recommendConditions),
                    batchLimit
            );
            if (batch.isEmpty()) {
                log.debug("장소 추천 종료 - 추가 조회 후보 없음");
                break;
            }

            log.debug(
                    "장소 추천 배치 시작 - 누적 조회 {}개, 대상={}",
                    searchedPlaces.size(),
                    summarizePlacesWithScore(batch, candidateRoutes)
            );

            accumulatedRecommendedPlaces.putAll(placeRecommender.recommendPlaces(batch, recommendConditions));
            searchedPlaces.addAll(batch);

            selectedCandidates = selectFinalPlaces(
                    candidateSelection,
                    searchedPlaces,
                    accumulatedRecommendedPlaces,
                    recommendConditions,
                    targetCount
            );

            log.debug(
                    "장소 추천 배치 완료 - 누적 조회 {}개, 현재 최종 후보 {}개",
                    searchedPlaces.size(),
                    selectedCandidates.getSelectedPlaces().size()
            );
        }

        return new RecommendationPlaceSearchResult(
                searchedPlaces,
                accumulatedRecommendedPlaces,
                selectedCandidates
        );
    }

    private SelectedCandidates selectFinalPlaces(
            final CandidateSelection candidateSelection,
            final List<Place> selectedPlaces,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final List<RecommendCondition> recommendConditions,
            final int targetCount
    ) {
        return finalCandidateSelector.select(
                candidateSelection,
                selectedPlaces,
                place -> satisfiesPlaceRequirements(place, recommendedPlaces, recommendConditions),
                targetCount
        );
    }

    private boolean satisfiesPlaceRequirements(
            final Place place,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        final CategorizedRecommendedPlaces categorizedRecommendedPlaces = recommendedPlaces.get(place);
        return categorizedRecommendedPlaces != null
                && hasAllRequiredPlaces(categorizedRecommendedPlaces, recommendConditions);
    }

    private boolean hasAllRequiredPlaces(
            final CategorizedRecommendedPlaces categorizedRecommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        final Map<RecommendCondition, List<RecommendedPlace>> categoryMap =
                categorizedRecommendedPlaces.getCategorizedPlaces();

        return recommendConditions.stream()
                .allMatch(condition -> categoryMap.containsKey(condition) && !categoryMap.get(condition).isEmpty());
    }

    private List<String> summarizePlacesWithScore(
            final List<Place> places,
            final Map<Place, Routes> candidateRoutes
    ) {
        return places.stream()
                .limit(5)
                .map(place -> place.getName() + "=" + candidateRoutes.get(place).calculateFairnessScore())
                .toList();
    }

}
