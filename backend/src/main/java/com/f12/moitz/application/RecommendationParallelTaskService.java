package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationsResponse;
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
import com.f12.moitz.application.dto.RecommendedLocationResponse;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationParallelTaskService {

    private final AsyncPlaceRecommender placeRecommender;
    private final AsyncRouteFinder routeFinder;

    private final LocationRecommender locationRecommender;
    private final PlaceFinder placeFinder;
    private final RecommendationMapper recommendationMapper;

    public RecommendationsResponse recommendLocation(final RecommendationRequest request) {
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getCategoryNames();
        final List<Place> startingPlaces = placeFinder.findPlacesByNames(request.startingPlaceNames());
        final RecommendedLocationResponse recommendedLocationResponse = locationRecommender.getRecommendedLocations(
                request.startingPlaceNames(),
                requirement
        );
        final Map<Place, String> generatedPlacesWithReason = locationRecommender.recommendLocations(recommendedLocationResponse);
        final List<Place> generatedPlaces = generatedPlacesWithReason.keySet().stream().toList();

        CompletableFuture<Map<Place, Routes>> routesFuture = findRoutesForAllAsync(startingPlaces, generatedPlaces);
        CompletableFuture<Map<Place, List<RecommendedPlace>>> recommendedPlacesFuture = placeRecommender.recommendPlacesAsync(
                generatedPlaces,
                requirement
        );

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

            return recommendationMapper.toResponse(startingPlaces, recommendation, generatedPlacesWithReason);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("비동기 병렬 처리 중 에러 발생", e);
        }
    }

    public CompletableFuture<Map<Place, Routes>> findRoutesForAllAsync(final List<Place> startingPlaces, final List<Place> generatedPlaces) {
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
