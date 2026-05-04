package com.f12.moitz.application.recommendation;

import com.f12.moitz.domain.recommendation.candidate.CandidateSelection;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.recommendation.candidate.RouteCandidate;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecommendationFlowLogger {

    public void logCandidateSelection(
            final List<SubwayStation> originStations,
            final RouteCandidatePreparationResult routeCandidatePreparationResult,
            final CandidateSelection candidateSelection
    ) {
        final List<Place> searchCandidatePlaces = candidateSelection.getSearchCandidatePlaces();

        log.debug(
                "공평성 후보 선정 - 출발역={}, initialPolicy={}, effectivePolicy={}, 전체 후보 {}개, 경로 계산 성공 {}개, 하드 필터 통과 {}개, 장소 탐색 대상 {}개, fallback={}",
                getPlaceNames(originStations),
                candidateSelection.getInitialPolicy(),
                candidateSelection.getEffectivePolicy(),
                routeCandidatePreparationResult.getCandidatePlaceCount(),
                routeCandidatePreparationResult.getRoutedPlaceCount(),
                candidateSelection.getAcceptableCount(),
                searchCandidatePlaces.size(),
                candidateSelection.isFallbackToSortedCandidates()
        );
        log.debug("공평성 후보 tag - {}", summarizeTagSelections(candidateSelection));
        log.debug("공평성 상위 후보 - {}", summarizePlacesWithScore(
                searchCandidatePlaces,
                routeCandidatePreparationResult.getCandidateRoutes()
        ));
    }

    public void logPlaceSearchResult(
            final List<Place> searchCandidatePlaces,
            final RecommendationPlaceSearchResult recommendationPlaceSearchResult
    ) {
        log.debug(
                "장소 추천 완료 - 탐색 대상 역 {}개 중 실제 조회 {}개, 결과 보유 역 {}개",
                searchCandidatePlaces.size(),
                recommendationPlaceSearchResult.getSearchedPlaceCount(),
                recommendationPlaceSearchResult.getRecommendedPlaceCount()
        );
    }

    public void logRecommendedCandidates(
            final List<Place> searchCandidatePlaces,
            final RecommendedCandidates recommendedCandidates,
            final Map<Place, Routes> candidateRoutes,
            final List<RecommendCondition> recommendConditions
    ) {
        final List<Place> recommendedCandidatePlaces = recommendedCandidates.getPlaces();
        log.debug(
                "추천 후보 확정 - 장소 탐색 대상 {}개, 추천 후보 {}개, 요구 조건={}",
                searchCandidatePlaces.size(),
                recommendedCandidatePlaces.size(),
                recommendConditions.stream().map(RecommendCondition::getTitle).toList()
        );

        if (recommendedCandidatePlaces.isEmpty()) {
            log.debug("추천 후보 없음 - 장소 탐색 대상 상위 후보 {}", summarizePlacesWithScore(searchCandidatePlaces, candidateRoutes));
            return;
        }

        log.debug("추천 후보 목록 - {}", summarizePlacesWithScore(recommendedCandidatePlaces, candidateRoutes));
    }

    private List<String> getPlaceNames(final List<? extends Place> places) {
        return places.stream()
                .map(Place::getName)
                .toList();
    }

    private Map<String, List<String>> summarizeTagSelections(final CandidateSelection candidateSelection) {
        return CandidateSelectionTag.orderedValues().stream()
                .collect(Collectors.toMap(
                        CandidateSelectionTag::getDescription,
                        tag -> summarizeCandidatesWithScore(candidateSelection.getCandidatesByTag(tag)),
                        (left, right) -> left,
                        java.util.LinkedHashMap::new
                ));
    }

    private List<String> summarizeCandidatesWithScore(final List<RouteCandidate> candidates) {
        return candidates.stream()
                .limit(5)
                .map(candidate -> candidate.getPlace().getName() + "=" + candidate.calculateFairnessScore())
                .toList();
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
