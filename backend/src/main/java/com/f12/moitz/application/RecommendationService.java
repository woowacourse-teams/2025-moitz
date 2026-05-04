package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResultResponse;
import com.f12.moitz.application.utils.RecommendationResponseMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelection;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionPolicy;
import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.Recommendation;
import com.f12.moitz.domain.recommendation.RecommendationReason;
import com.f12.moitz.domain.recommendation.RecommendedCandidateTravels;
import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.route.RouteOrigins;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.recommendation.repository.RecommendResultRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationService {

    private static final int PLACE_SEARCH_POOL_LIMIT = 50;
    private static final int RECOMMENDED_CANDIDATE_TARGET_COUNT = 5;

    private final RouteOriginPreparationService routeOriginPreparationService;
    private final RecommendedCandidateReasonService recommendedCandidateReasonService;
    private final RouteOriginDispersionService routeOriginDispersionService;
    private final RouteCandidatePreparationService routeCandidatePreparationService;
    private final RecommendationPlaceSearchService recommendationPlaceSearchService;
    private final RecommendedCandidateRouteService recommendedCandidateRouteService;
    private final RecommendationResponseMapper recommendationResponseMapper;
    private final RecommendResultRepository recommendResultRepository;
    private final CandidateSelectionPolicy candidateSelectionPolicy = new CandidateSelectionPolicy();
    private final RecommendationFlowLogger recommendationFlowLogger = new RecommendationFlowLogger();

    public RecommendationService(
            @Autowired final RouteOriginPreparationService routeOriginPreparationService,
            @Autowired final RecommendedCandidateReasonService recommendedCandidateReasonService,
            @Autowired final RouteOriginDispersionService routeOriginDispersionService,
            @Autowired final RouteCandidatePreparationService routeCandidatePreparationService,
            @Autowired final RecommendationPlaceSearchService recommendationPlaceSearchService,
            @Autowired final RecommendedCandidateRouteService recommendedCandidateRouteService,
            @Autowired final RecommendationResponseMapper recommendationResponseMapper,
            @Autowired final RecommendResultRepository recommendResultRepository
    ) {
        this.routeOriginPreparationService = routeOriginPreparationService;
        this.recommendedCandidateReasonService = recommendedCandidateReasonService;
        this.routeOriginDispersionService = routeOriginDispersionService;
        this.routeCandidatePreparationService = routeCandidatePreparationService;
        this.recommendationPlaceSearchService = recommendationPlaceSearchService;
        this.recommendedCandidateRouteService = recommendedCandidateRouteService;
        this.recommendationResponseMapper = recommendationResponseMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    public RecommendationCreateResponse recommendLocation(final RecommendationRequest request) {
        final StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        log.debug("추천 서비스 시작");

        stopWatch.start("공평한 후보역 선정");
        final List<RecommendCondition> recommendConditions = RecommendCondition.fromTitle(request.requirements());
        final RouteOriginPreparationResult routeOriginPreparationResult = prepareRouteOrigins(request);
        final List<SubwayStation> startingPlaces = routeOriginPreparationResult.getStartingPlaces();
        final RouteOrigins routeOrigins = routeOriginPreparationResult.getRouteOrigins();
        final DispersionPolicy dispersionPolicy = resolveDispersionPolicy(routeOrigins);
        final RouteCandidatePreparationResult routeCandidatePreparationResult = prepareRouteCandidates(
                startingPlaces,
                routeOrigins,
                dispersionPolicy
        );
        final Map<Place, Routes> candidateRoutes = routeCandidatePreparationResult.getCandidateRoutes();
        final CandidateSelection candidateSelection = selectCandidates(routeCandidatePreparationResult, dispersionPolicy);
        final List<Place> searchCandidatePlaces = candidateSelection.getSearchCandidatePlaces();
        recommendationFlowLogger.logCandidateSelection(startingPlaces, routeCandidatePreparationResult, candidateSelection);
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final RecommendationPlaceSearchResult recommendationPlaceSearchResult = searchRecommendedPlaces(
                candidateSelection,
                recommendConditions,
                candidateRoutes
        );
        final Map<Place, RecommendedPlaces> recommendedPlaces = recommendationPlaceSearchResult.getRecommendedPlaces();
        recommendationFlowLogger.logPlaceSearchResult(searchCandidatePlaces, recommendationPlaceSearchResult);
        stopWatch.stop();

        stopWatch.start("추천 후보 확정");
        final RecommendedCandidates recommendedCandidates = recommendationPlaceSearchResult.getRecommendedCandidates();
        recommendationFlowLogger.logRecommendedCandidates(
                searchCandidatePlaces,
                recommendedCandidates,
                candidateRoutes,
                recommendConditions
        );
        validateRecommendationCandidates(recommendedCandidates);
        final RecommendedCandidateTravels recommendedCandidateTravels = prepareRecommendedCandidateTravels(
                routeOrigins,
                recommendedCandidates
        );
        stopWatch.stop();

        stopWatch.start("추천 이유 생성");
        final Map<Place, RecommendationReason> reasonsByPlace = generateRecommendationReasons(recommendedCandidates);
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = createRecommendation(
                reasonsByPlace,
                recommendedPlaces,
                recommendedCandidateTravels,
                recommendedCandidates
        );
        stopWatch.stop();
        log.debug("추천 서비스 완료. {}", stopWatch.shortSummary());

        final String id = saveRecommendationResult(recommendConditions, startingPlaces, recommendation);
        return new RecommendationCreateResponse(id);
    }

    private RouteOriginPreparationResult prepareRouteOrigins(final RecommendationRequest request) {
        return routeOriginPreparationService.prepare(request.startingPlaceNames());
    }

    private DispersionPolicy resolveDispersionPolicy(final RouteOrigins routeOrigins) {
        return routeOriginDispersionService.resolve(routeOrigins);
    }

    private RouteCandidatePreparationResult prepareRouteCandidates(
            final List<SubwayStation> startingPlaces,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        return routeCandidatePreparationService.prepare(startingPlaces, routeOrigins, dispersionPolicy);
    }

    private CandidateSelection selectCandidates(
            final RouteCandidatePreparationResult routeCandidatePreparationResult,
            final DispersionPolicy dispersionPolicy
    ) {
        return candidateSelectionPolicy.select(
                routeCandidatePreparationResult.getRouteCandidates(),
                dispersionPolicy,
                PLACE_SEARCH_POOL_LIMIT
        );
    }

    private RecommendationPlaceSearchResult searchRecommendedPlaces(
            final CandidateSelection candidateSelection,
            final List<RecommendCondition> recommendConditions,
            final Map<Place, Routes> candidateRoutes
    ) {
        return recommendationPlaceSearchService.search(
                candidateSelection,
                recommendConditions,
                candidateRoutes,
                PLACE_SEARCH_POOL_LIMIT,
                RECOMMENDED_CANDIDATE_TARGET_COUNT
        );
    }

    private RecommendedCandidateTravels prepareRecommendedCandidateTravels(
            final RouteOrigins routeOrigins,
            final RecommendedCandidates recommendedCandidates
    ) {
        return recommendedCandidateRouteService.prepare(routeOrigins, recommendedCandidates);
    }

    private Map<Place, RecommendationReason> generateRecommendationReasons(
            final RecommendedCandidates recommendedCandidates
    ) {
        return recommendedCandidateReasonService.generate(recommendedCandidates);
    }

    private Recommendation createRecommendation(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlaces,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates
    ) {
        return Recommendation.create(
                reasonsByPlace,
                recommendedPlaces,
                recommendedCandidateTravels,
                recommendedCandidates
        );
    }

    private String saveRecommendationResult(
            final List<RecommendCondition> recommendConditions,
            final List<SubwayStation> startingPlaces,
            final Recommendation recommendation
    ) {
        return recommendResultRepository.saveAndReturnId(
                new Result(
                        recommendConditions,
                        startingPlaces,
                        recommendation
                )
        ).toHexString().toUpperCase();
    }

    private void validateRecommendationCandidates(final RecommendedCandidates recommendedCandidates) {
        if (recommendedCandidates.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.RECOMMENDATION_NOT_FOUND);
        }
    }

    public RecommendationResultResponse getById(final String id) {
        final Result result = recommendResultRepository.findById(parseObjectId(id))
                .orElseThrow(() -> new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT));
        return recommendationResponseMapper.toResponse(result);
    }

    private ObjectId parseObjectId(final String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }
    }

}
