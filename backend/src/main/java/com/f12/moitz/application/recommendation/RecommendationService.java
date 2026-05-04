package com.f12.moitz.application.recommendation;

import com.f12.moitz.application.dto.recommendation.RecommendationCreateResponse;
import com.f12.moitz.application.dto.recommendation.RecommendationRequest;
import com.f12.moitz.application.dto.recommendation.RecommendationResultResponse;
import com.f12.moitz.application.recommendation.utils.RecommendationFlowLogger;
import com.f12.moitz.application.subway.SubwayRouteService;
import com.f12.moitz.application.subway.SubwayStationService;
import com.f12.moitz.application.recommendation.utils.RecommendationResponseMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.Recommendation;
import com.f12.moitz.domain.recommendation.RecommendationReason;
import com.f12.moitz.domain.recommendation.RecommendedCandidateTravels;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelection;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionPolicy;
import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import com.f12.moitz.domain.recommendation.candidate.RouteCandidate;
import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.OriginDestinations;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.RouteOrigins;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.recommendation.repository.RecommendResultRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationService {

    private static final int PLACE_SEARCH_POOL_LIMIT = 50;
    private static final int RECOMMENDED_CANDIDATE_TARGET_COUNT = 5;

    private final SubwayStationService subwayStationService;
    private final SubwayRouteService subwayRouteService;
    private final RecommendationPlaceSearchService recommendationPlaceSearchService;
    private final RecommendationResponseMapper recommendationResponseMapper;
    private final RecommendResultRepository recommendResultRepository;
    private final CandidateSelectionPolicy candidateSelectionPolicy = new CandidateSelectionPolicy();
    private final RecommendationFlowLogger recommendationFlowLogger = new RecommendationFlowLogger();

    public RecommendationService(
            final SubwayStationService subwayStationService,
            final SubwayRouteService subwayRouteService,
            final RecommendationPlaceSearchService recommendationPlaceSearchService,
            final RecommendationResponseMapper recommendationResponseMapper,
            final RecommendResultRepository recommendResultRepository
    ) {
        this.subwayStationService = subwayStationService;
        this.subwayRouteService = subwayRouteService;
        this.recommendationPlaceSearchService = recommendationPlaceSearchService;
        this.recommendationResponseMapper = recommendationResponseMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    public RecommendationCreateResponse recommendLocation(final RecommendationRequest request) {
        final StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        log.debug("추천 서비스 시작");

        stopWatch.start("공평한 후보역 선정");
        final List<RecommendCondition> recommendConditions = RecommendCondition.fromTitle(request.requirements());
        final RouteOriginPreparationResult routeOriginPreparationResult = prepareRouteOrigins(request);
        final List<SubwayStation> originStations = routeOriginPreparationResult.getOriginStations();
        final RouteOrigins routeOrigins = routeOriginPreparationResult.getRouteOrigins();
        final DispersionPolicy dispersionPolicy = resolveDispersionPolicy(routeOrigins);
        final RouteCandidatePreparationResult routeCandidatePreparationResult = prepareRouteCandidates(
                originStations,
                routeOrigins,
                dispersionPolicy
        );
        final Map<Place, Routes> candidateRoutes = routeCandidatePreparationResult.getCandidateRoutes();
        final CandidateSelection candidateSelection = selectCandidates(routeCandidatePreparationResult, dispersionPolicy);
        final List<Place> searchCandidatePlaces = candidateSelection.getSearchCandidatePlaces();
        recommendationFlowLogger.logCandidateSelection(originStations, routeCandidatePreparationResult, candidateSelection);
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

        final String id = saveRecommendationResult(recommendConditions, originStations, recommendation);
        return new RecommendationCreateResponse(id);
    }

    private RouteOriginPreparationResult prepareRouteOrigins(final RecommendationRequest request) {
        final List<SubwayStation> originStations = getOriginStations(request.startingPlaceNames());
        return new RouteOriginPreparationResult(
                originStations,
                createRouteOrigins(originStations)
        );
    }

    private List<SubwayStation> getOriginStations(final List<String> names) {
        return names.stream()
                .map(name -> subwayStationService.findByName(name)
                        .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION)))
                .toList();
    }

    private RouteOrigins createRouteOrigins(final List<SubwayStation> originStations) {
        try {
            return new RouteOrigins(originStations);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION, getPlaceNames(originStations));
        }
    }

    private DispersionPolicy resolveDispersionPolicy(final RouteOrigins routeOrigins) {
        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsBetweenOrigins();
        final List<Route> pairRoutes = subwayRouteService.findRoutes(originDestinations.getValues());
        final DispersionPolicy dispersionPolicy = routeOrigins.resolveDispersionPolicy(pairRoutes);

        log.debug(
                "출발지 분산도 판정 - 출발역={}, pairRoutes={}개, policy={}",
                routeOrigins.getNames(),
                pairRoutes.size(),
                dispersionPolicy
        );
        return dispersionPolicy;
    }

    private RouteCandidatePreparationResult prepareRouteCandidates(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        final List<Place> candidatePlaces = getCandidatePlaces(originStations, routeOrigins, dispersionPolicy);
        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsTo(candidatePlaces);
        final Map<Place, Routes> candidateRoutes = findRoutesByDestination(originDestinations);
        final List<RouteCandidate> routeCandidates = createRouteCandidates(candidatePlaces, candidateRoutes);
        return new RouteCandidatePreparationResult(candidatePlaces, candidateRoutes, routeCandidates);
    }

    private List<Place> getCandidatePlaces(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        final int radiusKilometers = dispersionPolicy.candidateSearchRadiusKilometers();
        final List<SubwayStation> nearbyStations = subwayStationService.generateCandidatePlace(
                originStations,
                radiusKilometers
        );
        final List<Place> candidatePlaces = routeOrigins.excludeOriginsFrom(nearbyStations);

        log.debug(
                "후보역 1차 필터 완료 - policy={}, radius={}km, 후보 {}개",
                dispersionPolicy,
                radiusKilometers,
                candidatePlaces.size()
        );
        return candidatePlaces;
    }

    private Map<Place, Routes> findRoutesByDestination(final OriginDestinations originDestinations) {
        final List<Route> routes = subwayRouteService.findRoutes(originDestinations.getValues());
        return originDestinations.groupRoutesByDestination(routes);
    }

    private List<RouteCandidate> createRouteCandidates(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        return candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .map(place -> new RouteCandidate(place, candidateRoutes.get(place)))
                .toList();
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
        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsTo(
                recommendedCandidates.getPlaces()
        );
        final List<CandidateRoute> candidateRoutes = subwayRouteService.findCandidateRoutes(
                originDestinations.getValues()
        );
        return new RecommendedCandidateTravels(originDestinations.groupCandidateRoutesByDestination(candidateRoutes));
    }

    private Map<Place, RecommendationReason> generateRecommendationReasons(
            final RecommendedCandidates recommendedCandidates
    ) {
        return recommendedCandidates.createReasons();
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
            final List<SubwayStation> originStations,
            final Recommendation recommendation
    ) {
        return recommendResultRepository.saveAndReturnId(
                new Result(
                        recommendConditions,
                        originStations,
                        recommendation
                )
        ).toHexString().toUpperCase();
    }

    private List<String> getPlaceNames(final List<? extends Place> places) {
        return places.stream()
                .map(Place::getName)
                .toList();
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
