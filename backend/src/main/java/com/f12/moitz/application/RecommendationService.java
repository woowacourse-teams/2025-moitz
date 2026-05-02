package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResultResponse;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.CandidateSelectionPolicy;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationService {

    private static final int STARTING_VOTES = 0;
    private static final int PLACE_SEARCH_POOL_LIMIT = 50;
    private static final int FINAL_CANDIDATE_TARGET_COUNT = 5;

    private final RouteOriginPreparationService routeOriginPreparationService;
    private final RecommendedCandidateReasonService recommendedCandidateReasonService;
    private final RouteOriginDispersionService routeOriginDispersionService;
    private final RouteCandidatePreparationService routeCandidatePreparationService;
    private final RecommendationPlaceSearchService recommendationPlaceSearchService;
    private final SelectedCandidateRouteService selectedCandidateRouteService;
    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;
    private final CandidateSelectionPolicy candidateSelectionPolicy = new CandidateSelectionPolicy();

    public RecommendationService(
            @Autowired final RouteOriginPreparationService routeOriginPreparationService,
            @Autowired final RecommendedCandidateReasonService recommendedCandidateReasonService,
            @Autowired final RouteOriginDispersionService routeOriginDispersionService,
            @Autowired final RouteCandidatePreparationService routeCandidatePreparationService,
            @Autowired final RecommendationPlaceSearchService recommendationPlaceSearchService,
            @Autowired final SelectedCandidateRouteService selectedCandidateRouteService,
            @Autowired final RecommendationMapper recommendationMapper,
            @Autowired final RecommendResultRepository recommendResultRepository
    ) {
        this.routeOriginPreparationService = routeOriginPreparationService;
        this.recommendedCandidateReasonService = recommendedCandidateReasonService;
        this.routeOriginDispersionService = routeOriginDispersionService;
        this.routeCandidatePreparationService = routeCandidatePreparationService;
        this.recommendationPlaceSearchService = recommendationPlaceSearchService;
        this.selectedCandidateRouteService = selectedCandidateRouteService;
        this.recommendationMapper = recommendationMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    public RecommendationCreateResponse recommendLocation(final RecommendationRequest request) {
        final StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        log.debug("추천 서비스 시작");

        stopWatch.start("공평한 후보역 선정");
        final List<RecommendCondition> recommendConditions = RecommendCondition.fromTitle(request.requirements());
        final RouteOriginPreparationResult routeOriginPreparationResult = routeOriginPreparationService.prepare(
                request.startingPlaceNames()
        );
        final List<SubwayStation> startingPlaces = routeOriginPreparationResult.getStartingPlaces();
        final RouteOrigins routeOrigins = routeOriginPreparationResult.getRouteOrigins();
        final DispersionPolicy dispersionPolicy = routeOriginDispersionService.resolve(routeOrigins);
        final RouteCandidatePreparationResult routeCandidatePreparationResult = routeCandidatePreparationService.prepare(
                startingPlaces,
                routeOrigins,
                dispersionPolicy
        );
        final List<Place> candidatePlaces = routeCandidatePreparationResult.getCandidatePlaces();
        final Map<Place, Routes> candidateRoutes = routeCandidatePreparationResult.getCandidateRoutes();
        final CandidateSelection candidateSelection = candidateSelectionPolicy.select(
                routeCandidatePreparationResult.getRouteCandidates(),
                dispersionPolicy,
                PLACE_SEARCH_POOL_LIMIT
        );
        final List<Place> searchCandidatePlaces = candidateSelection.getSearchCandidatePlaces();
        logCandidateSelection(startingPlaces, candidatePlaces, candidateRoutes, candidateSelection);
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final RecommendationPlaceSearchResult recommendationPlaceSearchResult = recommendationPlaceSearchService.search(
                candidateSelection,
                recommendConditions,
                candidateRoutes,
                PLACE_SEARCH_POOL_LIMIT,
                FINAL_CANDIDATE_TARGET_COUNT
        );
        final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces = recommendationPlaceSearchResult.getRecommendedPlaces();
        log.debug(
                "장소 추천 완료 - 탐색 대상 역 {}개 중 실제 조회 {}개, 결과 보유 역 {}개",
                searchCandidatePlaces.size(),
                recommendationPlaceSearchResult.getSearchedPlaces().size(),
                recommendedPlaces.size()
        );
        stopWatch.stop();

        stopWatch.start("추천 후보 확정");
        final RecommendedCandidates recommendedCandidates = recommendationPlaceSearchResult.getRecommendedCandidates();
        final List<Place> recommendedCandidatePlaces = recommendedCandidates.getRecommendedCandidatePlaces();
        logRecommendedCandidates(searchCandidatePlaces, recommendedCandidatePlaces, candidateRoutes, recommendConditions);
        validateRecommendationCandidates(recommendedCandidatePlaces);
        final SelectedCandidateRouteResult selectedCandidateRouteResult = selectedCandidateRouteService.prepare(
                routeOrigins,
                recommendedCandidatePlaces,
                candidateRoutes
        );
        stopWatch.stop();

        stopWatch.start("추천 이유 생성");
        final Map<Place, ReasonAndDescription> generatedPlacesWithReason =
                recommendedCandidateReasonService.generate(recommendedCandidates);
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = recommendationMapper.toRecommendation(
                generatedPlacesWithReason,
                recommendedPlaces,
                selectedCandidateRouteResult.getRoutesByPlace(),
                selectedCandidateRouteResult.getCoursesByPlace(),
                recommendedCandidates.getTagsByPlace(),
                STARTING_VOTES,
                recommendConditions
        );
        stopWatch.stop();
        log.debug("추천 서비스 완료. {}", stopWatch.shortSummary());

        final String id = recommendResultRepository.saveAndReturnId(
                recommendationMapper.toResult(
                        recommendConditions,
                        startingPlaces,
                        recommendation
                )
        ).toHexString().toUpperCase();
        return new RecommendationCreateResponse(id);
    }

    private List<String> getPlaceNames(final List<? extends Place> places) {
        return places.stream()
                .map(Place::getName)
                .toList();
    }

    private void logCandidateSelection(
            final List<SubwayStation> startingPlaces,
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final CandidateSelection candidateSelection
    ) {
        final List<Place> searchCandidatePlaces = candidateSelection.getSearchCandidatePlaces();
        final long routeCalculatedCount = candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .count();

        log.debug(
                "공평성 후보 선정 - 출발역={}, initialPolicy={}, effectivePolicy={}, 전체 후보 {}개, 경로 계산 성공 {}개, 하드 필터 통과 {}개, 장소 탐색 대상 {}개, fallback={}",
                getPlaceNames(startingPlaces),
                candidateSelection.getInitialPolicy(),
                candidateSelection.getEffectivePolicy(),
                candidatePlaces.size(),
                routeCalculatedCount,
                candidateSelection.getAcceptableCount(),
                searchCandidatePlaces.size(),
                candidateSelection.isFallbackToSortedCandidates()
        );
        log.debug("공평성 후보 tag - {}", summarizeTagSelections(candidateSelection.getTagSelections()));
        log.debug("공평성 상위 후보 - {}", summarizePlacesWithScore(searchCandidatePlaces, candidateRoutes));
    }

    private Map<String, List<String>> summarizeTagSelections(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections
    ) {
        return tagSelections.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getDescription(),
                        entry -> summarizeCandidatesWithScore(entry.getValue()),
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

    private void logRecommendedCandidates(
            final List<Place> searchCandidatePlaces,
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final List<RecommendCondition> recommendConditions
    ) {
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

    private List<String> summarizePlacesWithScore(
            final List<Place> places,
            final Map<Place, Routes> candidateRoutes
    ) {
        return places.stream()
                .limit(5)
                .map(place -> place.getName() + "=" + candidateRoutes.get(place).calculateFairnessScore())
                .toList();
    }

    private void validateRecommendationCandidates(final List<Place> recommendedCandidatePlaces) {
        if (recommendedCandidatePlaces.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.RECOMMENDATION_NOT_FOUND);
        }
    }

    public RecommendationResultResponse getById(final String id) {
        final Result result = recommendResultRepository.findById(parseObjectId(id))
                .orElseThrow(() -> new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT));
        return recommendationMapper.toResponse(result);
    }

    private ObjectId parseObjectId(final String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }
    }

}
