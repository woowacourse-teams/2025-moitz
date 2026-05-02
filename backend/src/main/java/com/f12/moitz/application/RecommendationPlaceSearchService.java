package com.f12.moitz.application;

import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.CandidatePlaceSearchPolicy;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
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
    private final CandidatePlaceSearchPolicy candidatePlaceSearchPolicy = new CandidatePlaceSearchPolicy();

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
        RecommendedCandidates recommendedCandidates = selectRecommendedCandidates(
                candidateSelection,
                searchedPlaces,
                accumulatedRecommendedPlaces,
                recommendConditions,
                targetCount
        );

        while (searchedPlaces.size() < searchLimit) {
            if (recommendedCandidates.getRecommendedCandidatePlaces().size() >= targetCount) {
                log.debug("장소 추천 조기 종료 - 추천 후보 {}개 확보", targetCount);
                break;
            }

            final int batchLimit = Math.min(
                    PLACE_SEARCH_BATCH_SIZE,
                    searchLimit - searchedPlaces.size()
            );
            final List<Place> batch = candidatePlaceSearchPolicy.selectNextSearchPlaces(
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

            recommendedCandidates = selectRecommendedCandidates(
                    candidateSelection,
                    searchedPlaces,
                    accumulatedRecommendedPlaces,
                    recommendConditions,
                    targetCount
            );

            log.debug(
                    "장소 추천 배치 완료 - 누적 조회 {}개, 현재 추천 후보 {}개",
                    searchedPlaces.size(),
                    recommendedCandidates.getRecommendedCandidatePlaces().size()
            );
        }

        return new RecommendationPlaceSearchResult(
                searchedPlaces,
                accumulatedRecommendedPlaces,
                recommendedCandidates
        );
    }

    private RecommendedCandidates selectRecommendedCandidates(
            final CandidateSelection candidateSelection,
            final List<Place> searchedPlaces,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final List<RecommendCondition> recommendConditions,
            final int targetCount
    ) {
        return candidatePlaceSearchPolicy.select(
                candidateSelection,
                searchedPlaces,
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
                && categorizedRecommendedPlaces.satisfiesAll(recommendConditions);
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
