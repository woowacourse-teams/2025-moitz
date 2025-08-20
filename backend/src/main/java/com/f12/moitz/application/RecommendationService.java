package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RecommendedLocationResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.application.port.PlaceRecommender;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.f12.moitz.domain.entity.Result;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationService {
    private final PlaceFinder placeFinder;
    private final PlaceRecommender placeRecommender;
    private final LocationRecommender locationRecommender;
    private final RouteFinder routeFinder;

    private final RecommendationMapper recommendationMapper;
    private final RecommendResultRepository recommendResultRepository;

    public RecommendationService(
            @Autowired final PlaceFinder placeFinder,
            @Qualifier("placeRecommenderAdapter") final PlaceRecommender placeRecommender,
            @Autowired final LocationRecommender locationRecommender,
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder,
            @Autowired final RecommendationMapper recommendationMapper,
            @Autowired RecommendResultRepository recommendResultRepository
    ) {
        this.placeFinder = placeFinder;
        this.placeRecommender = placeRecommender;
        this.locationRecommender = locationRecommender;
        this.routeFinder = routeFinder;
        this.recommendationMapper = recommendationMapper;
        this.recommendResultRepository = recommendResultRepository;
    }

    public String recommendLocation(final RecommendationRequest request) {
        StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        stopWatch.start("지역 추천");
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getKeyword();

        final List<Place> startingPlaces = placeFinder.findPlacesByNames(request.startingPlaceNames());

        final RecommendedLocationResponse recommendedLocationResponse = locationRecommender.getRecommendedLocations(
                request.startingPlaceNames(),
                requirement
        );

        final Map<Place, String> generatedPlacesWithReason = locationRecommender.recommendLocations(
                recommendedLocationResponse);
        stopWatch.stop();

        final List<Place> generatedPlaces = generatedPlacesWithReason.keySet().stream().toList();

        stopWatch.start("모든 경로 찾기");
        final Map<Place, Routes> placeRoutes = findRoutesForAllAsync(startingPlaces, generatedPlaces);
        stopWatch.stop();

        stopWatch.start("기준 미달 경로 제거");
        removePlacesBeyondRange(placeRoutes, generatedPlacesWithReason);
        stopWatch.stop();

        stopWatch.start("장소 추천");
        final Map<Place, List<RecommendedPlace>> recommendedPlaces = placeRecommender.recommendPlaces(
                generatedPlaces,
                requirement
        );
        stopWatch.stop();

        stopWatch.start("Recommendation으로 변환");
        final Recommendation recommendation = toRecommendation(
                generatedPlacesWithReason,
                recommendedPlaces,
                placeRoutes
        );
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        Result result = recommendationMapper.toResult(startingPlaces, recommendation, generatedPlacesWithReason);
        return recommendResultRepository.saveAndReturnId(result);
    }

    public Map<Place, Routes> findRoutesForAllAsync(
            final List<Place> startingPlaces,
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

    private void removePlacesBeyondRange(
            final Map<Place, Routes> placeRoutes,
            final Map<Place, String> generatedPlaces
    ) {
        placeRoutes.forEach((key, value) -> {
            if (!value.isAcceptable()) {
                generatedPlaces.remove(key);
                log.debug("장소 제거 {}", key.getName());
            }
        });
    }

    private Recommendation toRecommendation(
            final Map<Place, String> generatedPlaces,
            final Map<Place, List<RecommendedPlace>> placeListMap,
            final Map<Place, Routes> placeRoutes
    ) {
        return new Recommendation(
                generatedPlaces.keySet().stream()
                        .map(place -> new Candidate(
                                place,
                                placeRoutes.get(place),
                                placeListMap.get(place)
                        ))
                        .toList()
        );
    }

    public RecommendationsResponse findResultById(final String id) {
        Result result = recommendResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디에 해당하는 결과를 찾을 수 없습니다."));
        return new RecommendationsResponse(result.getStartingPlaces(), result.getRecommendedLocations());
    }
}
