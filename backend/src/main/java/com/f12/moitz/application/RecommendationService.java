package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResultResponse;
import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.CandidateSelectionResult;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.FinalCandidateSelectionResult;
import com.f12.moitz.domain.FinalCandidateSelector;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.PlaceSearchCandidateSelector;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationService {

    private static final int STARTING_VOTES = 0;
    private static final int PLACE_SEARCH_BATCH_SIZE = 5;
    private static final int PLACE_SEARCH_POOL_LIMIT = 50;
    private static final int FINAL_CANDIDATE_TARGET_COUNT = 5;

    private final SubwayStationService subwayStationService;
    private final PlaceRecommender placeRecommender;
    private final LocationReasonGenerator locationReasonGenerator;
    private final RouteFinder routeFinder;
    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;
    private final PlaceSearchCandidateSelector placeSearchCandidateSelector = new PlaceSearchCandidateSelector();
    private final FinalCandidateSelector finalCandidateSelector = new FinalCandidateSelector();

    public RecommendationService(
            @Autowired final SubwayStationService subwayStationService,
            @Qualifier("placeRecommenderParallelAdapter") final PlaceRecommender placeRecommender,
            @Autowired final LocationReasonGenerator locationReasonGenerator,
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder,
            @Autowired final RecommendationMapper recommendationMapper,
            @Autowired final RecommendResultRepository recommendResultRepository
    ) {
        this.subwayStationService = subwayStationService;
        this.placeRecommender = placeRecommender;
        this.locationReasonGenerator = locationReasonGenerator;
        this.routeFinder = routeFinder;
        this.recommendationMapper = recommendationMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    public RecommendationCreateResponse recommendLocation(final RecommendationRequest request) {
        final StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        log.debug("추천 서비스 시작");

        stopWatch.start("공평한 후보역 선정");
        final List<RecommendCondition> recommendConditions = RecommendCondition.fromTitle(request.requirements());
        final List<SubwayStation> startingPlaces = getByNames(request.startingPlaceNames());
        final DispersionPolicy dispersionPolicy = resolveDispersionPolicy(startingPlaces);
        final List<Place> candidatePlaces = getCandidatePlaces(startingPlaces, dispersionPolicy);
        final List<StartEndPair> candidatePairs = createPairs(startingPlaces, candidatePlaces);
        final Map<Place, Routes> candidateRoutes = findRoutesForAll(candidatePairs);
        final CandidateSelectionResult candidateSelection = placeSearchCandidateSelector.select(
                toRouteCandidates(candidatePlaces, candidateRoutes),
                dispersionPolicy,
                PLACE_SEARCH_POOL_LIMIT
        );
        final List<Place> selectedPlaces = candidateSelection.getSelectedPlaces();
        logCandidateSelection(startingPlaces, candidatePlaces, candidateRoutes, candidateSelection);
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final PlaceSearchResult placeSearchResult = searchPlacesIncrementally(
                candidateSelection,
                recommendConditions,
                candidateRoutes
        );
        final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces = placeSearchResult.recommendedPlaces();
        log.debug(
                "장소 추천 완료 - 탐색 대상 역 {}개 중 실제 조회 {}개, 결과 보유 역 {}개",
                selectedPlaces.size(),
                placeSearchResult.searchedPlaces().size(),
                recommendedPlaces.size()
        );
        stopWatch.stop();

        stopWatch.start("최종 후보 확정");
        final FinalCandidateSelectionResult finalCandidateSelection = selectFinalPlaces(
                candidateSelection,
                placeSearchResult.searchedPlaces(),
                recommendedPlaces,
                recommendConditions
        );
        final List<Place> finalPlaces = finalCandidateSelection.getSelectedPlaces();
        logFinalCandidateSelection(selectedPlaces, finalPlaces, candidateRoutes, recommendConditions);
        validateRecommendationCandidates(finalPlaces);
        final List<StartEndPair> finalPairs = createPairs(startingPlaces, finalPlaces);
        final Map<Place, Courses> placeCourses = findCoursesForAll(finalPairs);
        final Map<Place, Routes> finalPlaceRoutes = finalPlaces.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        candidateRoutes::get
                ));
        stopWatch.stop();

        stopWatch.start("추천 이유 생성");
        final List<String> finalPlaceNames = getPlaceNames(finalPlaces);
        final Map<String, ReasonAndDescription> reasonsByPlaceName = locationReasonGenerator.generateReasons(
                finalPlaceNames,
                toTagsByPlaceName(finalCandidateSelection)
        );
        final Map<Place, ReasonAndDescription> generatedPlacesWithReason = finalPlaces.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        place -> reasonsByPlaceName.get(place.getName())
                ));
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = recommendationMapper.toRecommendation(
                generatedPlacesWithReason,
                recommendedPlaces,
                finalPlaceRoutes,
                placeCourses,
                finalCandidateSelection.getTagsByPlace(),
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

    private Map<String, List<CandidateSelectionTag>> toTagsByPlaceName(
            final FinalCandidateSelectionResult finalCandidateSelection
    ) {
        return finalCandidateSelection.getTagsByPlace().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getName(),
                        Entry::getValue,
                        (left, right) -> left,
                        java.util.LinkedHashMap::new
                ));
    }

    private List<SubwayStation> getByNames(final List<String> names) {
        return names.stream()
                .map(name -> subwayStationService.findByName(name)
                        .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION)))
                .toList();
    }

    private List<Place> getCandidatePlaces(
            final List<SubwayStation> startingPlaces,
            final DispersionPolicy dispersionPolicy
    ) {
        final int radiusKilometers = resolveCandidatePrefilterRadius(dispersionPolicy);
        final List<String> startingPlaceNames = getPlaceNames(startingPlaces);
        final List<Place> candidatePlaces = subwayStationService.generateCandidatePlace(
                        startingPlaces,
                        radiusKilometers
                ).stream()
                .filter(place -> !startingPlaceNames.contains(place.getName()))
                .map(Place.class::cast)
                .toList();

        log.debug(
                "후보역 1차 필터 완료 - policy={}, radius={}km, 후보 {}개",
                dispersionPolicy,
                radiusKilometers,
                candidatePlaces.size()
        );
        return candidatePlaces;
    }

    private int resolveCandidatePrefilterRadius(final DispersionPolicy dispersionPolicy) {
        return switch (dispersionPolicy) {
            case TIER_1, TIER_2 -> 10;
            case TIER_3 -> 20;
            case TIER_4 -> 30;
            case TIER_5 -> 40;
        };
    }

    private List<StartEndPair> createPairs(
            final List<? extends Place> startingPlaces,
            final List<Place> generatedPlaces
    ) {
        return generatedPlaces.stream()
                .flatMap(endPlace -> startingPlaces.stream()
                        .map(startPlace -> new StartEndPair(startPlace, endPlace)))
                .toList();
    }

    private Map<Place, Routes> findRoutesForAll(final List<StartEndPair> allPairs) {
        final List<Route> allRoutes = routeFinder.findRoutes(allPairs);
        return collectByPlace(allPairs, allRoutes, Routes::new);
    }

    private DispersionPolicy resolveDispersionPolicy(final List<SubwayStation> startingPlaces) {
        final List<StartEndPair> startingPairs = createStartingPlacePairs(startingPlaces);
        final List<Route> pairRoutes = routeFinder.findRoutes(startingPairs);
        final int pairMaxTravelTime = pairRoutes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .max()
                .orElse(0);
        final double pairAverageTravelTime = pairRoutes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .average()
                .orElse(0.0);
        final long longPairCount = pairRoutes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .filter(minutes -> minutes >= DispersionPolicy.LONG_PAIR_TRAVEL_TIME_MINUTES)
                .count();
        final DispersionPolicy dispersionPolicy = DispersionPolicy.resolve(
                startingPlaces.size(),
                pairMaxTravelTime,
                pairAverageTravelTime,
                longPairCount
        );

        log.debug(
                "출발지 분산도 판정 - 출발역={}, pairMax={}분, pairAvg={}분, longPairCount={}, policy={}",
                getPlaceNames(startingPlaces),
                pairMaxTravelTime,
                String.format(java.util.Locale.US, "%.1f", pairAverageTravelTime),
                longPairCount,
                dispersionPolicy
        );
        return dispersionPolicy;
    }

    private List<StartEndPair> createStartingPlacePairs(final List<SubwayStation> startingPlaces) {
        return IntStream.range(0, startingPlaces.size())
                .boxed()
                .flatMap(left -> IntStream.range(left + 1, startingPlaces.size())
                        .mapToObj(right -> new StartEndPair(startingPlaces.get(left), startingPlaces.get(right))))
                .toList();
    }

    private Map<Place, Courses> findCoursesForAll(final List<StartEndPair> allPairs) {
        final List<Course> allCourses = routeFinder.findCourses(allPairs);
        return collectByPlace(allPairs, allCourses, Courses::new);
    }

    private <T, U> Map<Place, U> collectByPlace(
            final List<StartEndPair> allPairs,
            final List<T> elements,
            final Function<List<T>, U> creator
    ) {
        return IntStream.range(0, allPairs.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> allPairs.get(i).end(),
                        Collectors.mapping(
                                elements::get,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> creator.apply(entry.getValue())
                ));
    }

    private List<RouteCandidate> toRouteCandidates(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        return candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .map(place -> new RouteCandidate(place, candidateRoutes.get(place)))
                .toList();
    }

    private void logCandidateSelection(
            final List<SubwayStation> startingPlaces,
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final CandidateSelectionResult candidateSelection
    ) {
        final List<Place> selectedPlaces = candidateSelection.getSelectedPlaces();
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
                selectedPlaces.size(),
                candidateSelection.isFallbackToSortedCandidates()
        );
        log.debug("공평성 후보 tag - {}", summarizeTagSelections(candidateSelection.getTagSelections()));
        log.debug("공평성 상위 후보 - {}", summarizePlacesWithScore(selectedPlaces, candidateRoutes));
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

    private FinalCandidateSelectionResult selectFinalPlaces(
            final CandidateSelectionResult candidateSelection,
            final List<Place> selectedPlaces,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        return finalCandidateSelector.select(
                candidateSelection,
                selectedPlaces,
                place -> satisfiesPlaceRequirements(place, recommendedPlaces, recommendConditions),
                FINAL_CANDIDATE_TARGET_COUNT
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
        final Map<RecommendCondition, List<com.f12.moitz.domain.RecommendedPlace>> categoryMap =
                categorizedRecommendedPlaces.getCategorizedPlaces();

        return recommendConditions.stream()
                .allMatch(condition -> categoryMap.containsKey(condition) && !categoryMap.get(condition).isEmpty());
    }

    private void logFinalCandidateSelection(
            final List<Place> selectedPlaces,
            final List<Place> finalPlaces,
            final Map<Place, Routes> candidateRoutes,
            final List<RecommendCondition> recommendConditions
    ) {
        log.debug(
                "최종 후보 확정 - 장소 탐색 대상 {}개, 최종 후보 {}개, 요구 조건={}",
                selectedPlaces.size(),
                finalPlaces.size(),
                recommendConditions.stream().map(RecommendCondition::getTitle).toList()
        );

        if (finalPlaces.isEmpty()) {
            log.debug("최종 후보 없음 - 장소 탐색 대상 상위 후보 {}", summarizePlacesWithScore(selectedPlaces, candidateRoutes));
            return;
        }

        log.debug("최종 후보 목록 - {}", summarizePlacesWithScore(finalPlaces, candidateRoutes));
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

    private void validateRecommendationCandidates(final List<Place> finalPlaces) {
        if (finalPlaces.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.RECOMMENDATION_NOT_FOUND);
        }
    }

    private PlaceSearchResult searchPlacesIncrementally(
            final CandidateSelectionResult candidateSelection,
            final List<RecommendCondition> recommendConditions,
            final Map<Place, Routes> candidateRoutes
    ) {
        final Map<Place, CategorizedRecommendedPlaces> accumulatedRecommendedPlaces = new java.util.LinkedHashMap<>();
        final List<Place> searchedPlaces = new java.util.ArrayList<>();

        while (searchedPlaces.size() < PLACE_SEARCH_POOL_LIMIT) {
            final FinalCandidateSelectionResult updatedFinalSelection = selectFinalPlaces(
                    candidateSelection,
                    searchedPlaces,
                    accumulatedRecommendedPlaces,
                    recommendConditions
            );
            if (updatedFinalSelection.getSelectedPlaces().size() >= FINAL_CANDIDATE_TARGET_COUNT) {
                log.debug("장소 추천 조기 종료 - 최종 후보 {}개 확보", FINAL_CANDIDATE_TARGET_COUNT);
                break;
            }

            final int batchLimit = Math.min(
                    PLACE_SEARCH_BATCH_SIZE,
                    PLACE_SEARCH_POOL_LIMIT - searchedPlaces.size()
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

            final FinalCandidateSelectionResult currentFinalSelection = selectFinalPlaces(
                    candidateSelection,
                    searchedPlaces,
                    accumulatedRecommendedPlaces,
                    recommendConditions
            );

            log.debug(
                    "장소 추천 배치 완료 - 누적 조회 {}개, 현재 최종 후보 {}개",
                    searchedPlaces.size(),
                    currentFinalSelection.getSelectedPlaces().size()
            );
        }

        return new PlaceSearchResult(
                searchedPlaces,
                accumulatedRecommendedPlaces
        );
    }

    private record PlaceSearchResult(
            List<Place> searchedPlaces,
            Map<Place, CategorizedRecommendedPlaces> recommendedPlaces
    ) {

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
