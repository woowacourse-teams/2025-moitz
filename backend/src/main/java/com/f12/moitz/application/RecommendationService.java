package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResultResponse;
import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.FinalCandidateSelectionResult;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.PlaceSearchCandidateSelector;
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
import java.util.Map.Entry;
import java.util.function.Function;
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

    private final SubwayStationService subwayStationService;
    private final LocationReasonGenerator locationReasonGenerator;
    private final RouteOriginDispersionService routeOriginDispersionService;
    private final RouteCandidatePreparationService routeCandidatePreparationService;
    private final RecommendationPlaceSearchService recommendationPlaceSearchService;
    private final SelectedCandidateRouteService selectedCandidateRouteService;
    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;
    private final PlaceSearchCandidateSelector placeSearchCandidateSelector = new PlaceSearchCandidateSelector();

    public RecommendationService(
            @Autowired final SubwayStationService subwayStationService,
            @Autowired final LocationReasonGenerator locationReasonGenerator,
            @Autowired final RouteOriginDispersionService routeOriginDispersionService,
            @Autowired final RouteCandidatePreparationService routeCandidatePreparationService,
            @Autowired final RecommendationPlaceSearchService recommendationPlaceSearchService,
            @Autowired final SelectedCandidateRouteService selectedCandidateRouteService,
            @Autowired final RecommendationMapper recommendationMapper,
            @Autowired final RecommendResultRepository recommendResultRepository
    ) {
        this.subwayStationService = subwayStationService;
        this.locationReasonGenerator = locationReasonGenerator;
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
        final List<SubwayStation> startingPlaces = getByNames(request.startingPlaceNames());
        final RouteOrigins routeOrigins = createRouteOrigins(startingPlaces);
        final DispersionPolicy dispersionPolicy = routeOriginDispersionService.resolve(routeOrigins);
        final RouteCandidatePreparationResult routeCandidatePreparationResult = routeCandidatePreparationService.prepare(
                startingPlaces,
                routeOrigins,
                dispersionPolicy
        );
        final List<Place> candidatePlaces = routeCandidatePreparationResult.getCandidatePlaces();
        final Map<Place, Routes> candidateRoutes = routeCandidatePreparationResult.getCandidateRoutes();
        final CandidateSelection candidateSelection = placeSearchCandidateSelector.select(
                routeCandidatePreparationResult.getRouteCandidates(),
                dispersionPolicy,
                PLACE_SEARCH_POOL_LIMIT
        );
        final List<Place> selectedPlaces = candidateSelection.getSelectedPlaces();
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
                selectedPlaces.size(),
                recommendationPlaceSearchResult.getSearchedPlaces().size(),
                recommendedPlaces.size()
        );
        stopWatch.stop();

        stopWatch.start("최종 후보 확정");
        final FinalCandidateSelectionResult finalCandidateSelection = recommendationPlaceSearchResult.getFinalCandidateSelection();
        final List<Place> finalPlaces = finalCandidateSelection.getSelectedPlaces();
        logFinalCandidateSelection(selectedPlaces, finalPlaces, candidateRoutes, recommendConditions);
        validateRecommendationCandidates(finalPlaces);
        final SelectedCandidateRouteResult selectedCandidateRouteResult = selectedCandidateRouteService.prepare(
                routeOrigins,
                finalPlaces,
                candidateRoutes
        );
        stopWatch.stop();

        stopWatch.start("추천 이유 생성");
        final List<String> finalPlaceNames = getPlaceNames(finalPlaces);
        final Map<String, ReasonAndDescription> reasonsByPlaceName = locationReasonGenerator.generateReasons(
                finalPlaceNames,
                toTagsByPlaceName(finalCandidateSelection)
        );
        final Map<Place, ReasonAndDescription> generatedPlacesWithReason = mapReasonsByPlace(
                finalPlaces,
                reasonsByPlaceName
        );
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = recommendationMapper.toRecommendation(
                generatedPlacesWithReason,
                recommendedPlaces,
                selectedCandidateRouteResult.getRoutesByPlace(),
                selectedCandidateRouteResult.getCoursesByPlace(),
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

    private Map<Place, ReasonAndDescription> mapReasonsByPlace(
            final List<Place> places,
            final Map<String, ReasonAndDescription> reasonsByPlaceName
    ) {
        if (reasonsByPlaceName == null) {
            throw new IllegalStateException("추천 이유 생성 결과가 null입니다.");
        }
        return places.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        place -> getReason(place, reasonsByPlaceName)
                ));
    }

    private ReasonAndDescription getReason(
            final Place place,
            final Map<String, ReasonAndDescription> reasonsByPlaceName
    ) {
        final ReasonAndDescription reason = reasonsByPlaceName.get(place.getName());
        if (reason == null) {
            throw new IllegalStateException("추천 이유 생성 결과가 누락되었습니다. placeName=" + place.getName());
        }
        return reason;
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

    private RouteOrigins createRouteOrigins(final List<SubwayStation> startingPlaces) {
        try {
            return new RouteOrigins(startingPlaces);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION, getPlaceNames(startingPlaces));
        }
    }

    private void logCandidateSelection(
            final List<SubwayStation> startingPlaces,
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes,
            final CandidateSelection candidateSelection
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
