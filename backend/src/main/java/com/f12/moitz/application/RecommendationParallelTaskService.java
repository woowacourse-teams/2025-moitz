package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RecommendedLocationResponse;
import com.f12.moitz.application.port.AsyncPlaceRecommender;
import com.f12.moitz.application.port.AsyncRouteFinder;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class RecommendationParallelTaskService {

    private final AsyncPlaceRecommender placeRecommender;
    private final AsyncRouteFinder routeFinder;

    private final LocationRecommender locationRecommender;
    private final PlaceFinder placeFinder;
    private final RecommendationMapper recommendationMapper;

    public RecommendationParallelTaskService(
            final AsyncPlaceRecommender placeRecommender,
            @Qualifier("subwayRouteFinderAsyncAdapter") final AsyncRouteFinder routeFinder,
            final LocationRecommender locationRecommender,
            final PlaceFinder placeFinder,
            final RecommendationMapper recommendationMapper
    ) {
        this.placeRecommender = placeRecommender;
        this.routeFinder = routeFinder;
        this.locationRecommender = locationRecommender;
        this.placeFinder = placeFinder;
        this.recommendationMapper = recommendationMapper;
    }

    public RecommendationsResponse recommendLocation(final RecommendationRequest request) {
        StopWatch stopWatch = new StopWatch("추천 서비스 전체");
        stopWatch.start("지역 추천");
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getCategoryNames();
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
        CompletableFuture<Map<Place, Routes>> routesFuture = findRoutesForAllAsync(startingPlaces, generatedPlaces);
        stopWatch.stop();

        stopWatch.start("장소 추천 비동기");
        CompletableFuture<Map<Place, List<RecommendedPlace>>> recommendedPlacesFuture = placeRecommender.recommendPlacesAsync(
                generatedPlaces,
                requirement
        );
        stopWatch.stop();

        stopWatch.start("Recommendation 변환");
        CompletableFuture.allOf(routesFuture, recommendedPlacesFuture).join();

        try {
            Map<Place, Routes> placeRoutes = routesFuture.get();
            removePlacesBeyondRange(placeRoutes, generatedPlacesWithReason);

            Map<Place, List<RecommendedPlace>> recommendedPlaces = recommendedPlacesFuture.get();

            final Recommendation recommendation = toRecommendation(
                    generatedPlacesWithReason,
                    recommendedPlaces,
                    placeRoutes
            );

            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());

            return recommendationMapper.toResponse(startingPlaces, recommendation, generatedPlacesWithReason);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("비동기 병렬 처리 중 에러 발생", e);
        }
    }

    public CompletableFuture<Map<Place, Routes>> findRoutesForAllAsync(
            final List<Place> startingPlaces,
            final List<Place> generatedPlaces
    ) {
        final List<StartEndPair> allPairs = generatedPlaces.stream()
                .flatMap(endPlace -> startingPlaces.stream()
                        .map(startPlace -> new StartEndPair(startPlace, endPlace)))
                .toList();

        return routeFinder.findRoutesAsync(allPairs)
                .thenApply(allRoutes -> IntStream.range(0, allPairs.size())
                        .boxed()
                        .collect(Collectors.groupingBy(
                                i -> allPairs.get(i).end(),
                                Collectors.mapping(allRoutes::get, Collectors.toList())
                        ))
                        .entrySet().stream()
                        .collect(Collectors.toMap(Entry::getKey, entry -> new Routes(entry.getValue()))));
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

}
