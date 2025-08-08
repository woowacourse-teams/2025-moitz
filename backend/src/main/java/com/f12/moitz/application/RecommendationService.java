package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.port.Recommender;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecommendationService {

    private final Recommender recommender;
    private final RouteFinder routeFinder;

    private final RecommendationMapper recommendationMapper;

    public RecommendationService(
            @Autowired final Recommender recommender,
            @Qualifier("routeFinderAsyncAdapter") final RouteFinder routeFinder,
            @Autowired final RecommendationMapper recommendationMapper
    ) {
        this.recommender = recommender;
        this.routeFinder = routeFinder;
        this.recommendationMapper = recommendationMapper;
    }

    public RecommendationsResponse recommendLocation(final RecommendationRequest request) {
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getCategoryNames();

        final List<Place> startingPlaces = recommender.findPlacesByNames(request.startingPlaceNames());
        final Map<Place, String> generatedPlacesWithReason = recommender.recommendLocations(
                request.startingPlaceNames(),
                requirement
        );
        final List<Place> generatedPlaces = generatedPlacesWithReason.keySet().stream().toList();

        final Map<Place, Routes> placeRoutes = findRoutesForAllAsync(startingPlaces, generatedPlaces);
        removePlacesBeyondRange(placeRoutes, generatedPlacesWithReason);

        final Map<Place, List<RecommendedPlace>> recommendedPlaces = recommender.recommendPlaces(
                generatedPlaces,
                requirement
        );

        final Recommendation recommendation = toRecommendation(
                generatedPlacesWithReason,
                recommendedPlaces,
                placeRoutes
        );

        return recommendationMapper.toResponse(startingPlaces, recommendation, generatedPlacesWithReason);
    }

    public Map<Place, Routes> findRoutesForAllAsync(final List<Place> startingPlaces, final List<Place> generatedPlaces) {
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

}
