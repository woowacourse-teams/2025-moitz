package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.CategorizedRecommendedPlaces;
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

    private final SubwayStationService subwayStationService;
    private final PlaceRecommender placeRecommender;
    private final LocationRecommender locationRecommender;
    private final RouteFinder routeFinder;

    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;

    public RecommendationService(
            @Autowired final SubwayStationService subwayStationService,
            @Qualifier("placeRecommenderAdapter") final PlaceRecommender placeRecommender,
            @Autowired final LocationRecommender locationRecommender,
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder,
            @Autowired final RecommendationMapper recommendationMapper,
            @Autowired final RecommendResultRepository recommendResultRepository
    ) {
        this.subwayStationService = subwayStationService;
        this.placeRecommender = placeRecommender;
        this.locationRecommender = locationRecommender;
        this.routeFinder = routeFinder;
        this.recommendationMapper = recommendationMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    public String recommendLocation(final RecommendationRequest request) {
        StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        log.debug("추천 서비스 시작");

        stopWatch.start("지역 추천");
        final List<RecommendCondition> recommendConditions = RecommendCondition.fromTitle(request.requirements());
        final List<String> requirements = RecommendCondition.getRequirements(recommendConditions);
        log.info(requirements.toString());
        final List<SubwayStation> startingPlaces = getByNames(request.startingPlaceNames());
        final List<SubwayStation> candidatePlaces = subwayStationService.generateCandidatePlace(startingPlaces);

        final List<String> startingPlaceNames = getPlaceNames(startingPlaces);
        final List<String> candidatePlaceNames = getPlaceNames(candidatePlaces);

        final RecommendedLocationsResponse recommendedLocationsResponse = locationRecommender.recommendLocations(
                startingPlaceNames,
                candidatePlaceNames,
                requirements
        );
        final Map<Place, ReasonAndDescription> generatedPlacesWithReason = recommendedLocationsResponse.recommendations()
                .stream()
                .collect(Collectors.toMap(
                        recommendation -> subwayStationService.getByName(recommendation.locationName()),
                        recommendation -> new ReasonAndDescription(
                                recommendation.reason(),
                                recommendation.description()
                        )
                ));
        stopWatch.stop();

        stopWatch.start("모든 경로 찾기");
        List<Place> generatedPlaces = generatedPlacesWithReason.keySet().stream().toList();
        final Map<Place, Routes> placeRoutes = findRoutesForAllAsync(startingPlaces, generatedPlaces);
        stopWatch.stop();

        stopWatch.start("기준 미달 경로 제거");
        final Map<Place, ReasonAndDescription> filteredPlacesWithReason = removePlacesBeyondRange(placeRoutes, generatedPlacesWithReason);
        generatedPlaces = filteredPlacesWithReason.keySet().stream().toList();
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces = placeRecommender.recommendPlaces(
                generatedPlaces,
                requirements
        );
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = recommendationMapper.toRecommendation(
                filteredPlacesWithReason,
                recommendedPlaces,
                placeRoutes
        );
        stopWatch.stop();
        log.debug("추천 서비스 완료. {}", stopWatch.shortSummary());

        return recommendResultRepository.saveAndReturnId(
                recommendationMapper.toResult(
                        recommendConditions,
                        startingPlaces,
                        recommendation
                )
        ).toHexString().toUpperCase();
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

    private Map<Place, Routes> findRoutesForAllAsync(
            final List<? extends Place> startingPlaces,
            final List<Place> generatedPlaces
    ) {
        final List<StartEndPair> allPairs = generatedPlaces.stream()
                .flatMap(endPlace -> startingPlaces.stream()
                        .map(startPlace -> new StartEndPair(startPlace, endPlace)))
                .collect(Collectors.toList());

        final List<Route> allRoutes = routeFinder.findRoutes(allPairs);

        return IntStream.range(0, allPairs.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> allPairs.get(i).end(),
                        Collectors.mapping(
                                allRoutes::get,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new Routes(entry.getValue())
                ));
    }

    private Map<Place, ReasonAndDescription> removePlacesBeyondRange(
            final Map<Place, Routes> placeRoutes,
            final Map<Place, ReasonAndDescription> generatedPlaces
    ) {
        return generatedPlaces.entrySet().stream()
                .filter(entry -> {
                    Place place = entry.getKey();
                    return placeRoutes.containsKey(place) && placeRoutes.get(place).isAcceptable();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public RecommendationsResponse getById(final String id) {
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
