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
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Courses;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.FairnessScore;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.Route;
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
    private static final int PLACE_SEARCH_POOL_LIMIT = 30;
    private static final int FINAL_CANDIDATE_TARGET_COUNT = 5;

    private final SubwayStationService subwayStationService;
    private final PlaceRecommender placeRecommender;
    private final LocationReasonGenerator locationReasonGenerator;
    private final RouteFinder routeFinder;
    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;

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
        final List<Place> candidatePlaces = getAllCandidatePlaces(startingPlaces);
        final List<StartEndPair> candidatePairs = createPairs(startingPlaces, candidatePlaces);
        final Map<Place, Routes> candidateRoutes = findRoutesForAll(candidatePairs);
        final CandidateSelection candidateSelection = selectCandidatePlacesForPlaceSearch(
                candidatePlaces,
                candidateRoutes,
                dispersionPolicy
        );
        final List<Place> selectedPlaces = candidateSelection.selectedPlaces();
        logCandidateSelection(startingPlaces, candidatePlaces, candidateRoutes, candidateSelection);
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final PlaceSearchResult placeSearchResult = searchPlacesIncrementally(
                selectedPlaces,
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
        final List<Place> finalPlaces = filterByRequirements(
                placeSearchResult.searchedPlaces(),
                recommendedPlaces,
                recommendConditions
        );
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
        final List<String> startingPlaceNames = getPlaceNames(startingPlaces);
        final List<String> finalPlaceNames = getPlaceNames(finalPlaces);
        final Map<String, ReasonAndDescription> reasonsByPlaceName = locationReasonGenerator.generateReasons(
                startingPlaceNames,
                finalPlaceNames,
                recommendConditions
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

    private List<SubwayStation> getByNames(final List<String> names) {
        return names.stream()
                .map(name -> subwayStationService.findByName(name)
                        .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION)))
                .toList();
    }

    private List<Place> getAllCandidatePlaces(final List<SubwayStation> startingPlaces) {
        final List<String> startingPlaceNames = getPlaceNames(startingPlaces);
        return subwayStationService.getAll().stream()
                .filter(place -> !startingPlaceNames.contains(place.getName()))
                .map(Place.class::cast)
                .toList();
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

    private CandidateSelection selectCandidatePlacesForPlaceSearch(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final DispersionPolicy dispersionPolicy
    ) {
        final List<Place> sortedCandidates = candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .sorted((left, right) -> compareFairness(candidateRoutes.get(left), candidateRoutes.get(right)))
                .toList();

        for (DispersionPolicy candidatePolicy : dispersionPolicy.relaxations()) {
            final List<Place> acceptableCandidates = sortedCandidates.stream()
                    .filter(place -> candidateRoutes.get(place).isAcceptable(candidatePolicy))
                    .toList();

            if (!acceptableCandidates.isEmpty()) {
                return new CandidateSelection(
                        acceptableCandidates.stream()
                                .limit(PLACE_SEARCH_POOL_LIMIT)
                                .toList(),
                        dispersionPolicy,
                        candidatePolicy,
                        acceptableCandidates.size(),
                        false
                );
            }
        }

        return new CandidateSelection(
                sortedCandidates.stream()
                        .limit(PLACE_SEARCH_POOL_LIMIT)
                        .toList(),
                dispersionPolicy,
                DispersionPolicy.TIER_5,
                0,
                true
        );
    }

    private void logCandidateSelection(
            final List<SubwayStation> startingPlaces,
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final CandidateSelection candidateSelection
    ) {
        final List<Place> selectedPlaces = candidateSelection.selectedPlaces();
        final List<Place> sortableCandidates = candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .sorted((left, right) -> compareFairness(candidateRoutes.get(left), candidateRoutes.get(right)))
                .toList();

        log.debug(
                "공평성 후보 선정 - 출발역={}, initialPolicy={}, effectivePolicy={}, 전체 후보 {}개, 경로 계산 성공 {}개, 하드 필터 통과 {}개, 장소 탐색 대상 {}개, fallback={}",
                getPlaceNames(startingPlaces),
                candidateSelection.initialPolicy(),
                candidateSelection.effectivePolicy(),
                candidatePlaces.size(),
                sortableCandidates.size(),
                candidateSelection.acceptableCount(),
                selectedPlaces.size(),
                candidateSelection.fallbackToSortedCandidates()
        );
        log.debug("공평성 상위 후보 - {}", summarizePlacesWithScore(selectedPlaces, candidateRoutes));
    }

    private int compareFairness(final Routes left, final Routes right) {
        final FairnessScore leftScore = left.calculateFairnessScore();
        final FairnessScore rightScore = right.calculateFairnessScore();
        return leftScore.compareTo(rightScore);
    }

    private List<Place> filterByRequirements(
            final List<Place> selectedPlaces,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        return selectedPlaces.stream()
                .filter(place -> recommendedPlaces.get(place) != null)
                .filter(place -> hasAllRequiredPlaces(recommendedPlaces.get(place), recommendConditions))
                .limit(FINAL_CANDIDATE_TARGET_COUNT)
                .toList();
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
            final List<Place> selectedPlaces,
            final List<RecommendCondition> recommendConditions,
            final Map<Place, Routes> candidateRoutes
    ) {
        final Map<Place, CategorizedRecommendedPlaces> accumulatedRecommendedPlaces = new java.util.LinkedHashMap<>();
        final List<Place> searchedPlaces = new java.util.ArrayList<>();

        for (int start = 0; start < selectedPlaces.size(); start += PLACE_SEARCH_BATCH_SIZE) {
            final int end = Math.min(start + PLACE_SEARCH_BATCH_SIZE, selectedPlaces.size());
            final List<Place> batch = selectedPlaces.subList(start, end);

            log.debug(
                    "장소 추천 배치 시작 - batch={}~{}, 대상={}",
                    start,
                    end - 1,
                    summarizePlacesWithScore(batch, candidateRoutes)
            );

            accumulatedRecommendedPlaces.putAll(placeRecommender.recommendPlaces(batch, recommendConditions));
            searchedPlaces.addAll(batch);

            final List<Place> currentFinalPlaces = filterByRequirements(
                    searchedPlaces,
                    accumulatedRecommendedPlaces,
                    recommendConditions
            );

            log.debug(
                    "장소 추천 배치 완료 - 누적 조회 {}개, 현재 최종 후보 {}개",
                    searchedPlaces.size(),
                    currentFinalPlaces.size()
            );

            if (currentFinalPlaces.size() >= FINAL_CANDIDATE_TARGET_COUNT) {
                log.debug("장소 추천 조기 종료 - 목표 후보 {}개 확보", FINAL_CANDIDATE_TARGET_COUNT);
                break;
            }
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

    private record CandidateSelection(
            List<Place> selectedPlaces,
            DispersionPolicy initialPolicy,
            DispersionPolicy effectivePolicy,
            long acceptableCount,
            boolean fallbackToSortedCandidates
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
