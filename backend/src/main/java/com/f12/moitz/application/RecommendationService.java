package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.port.Recommender;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.utils.RecommendationMapper;
import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.Routes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final Recommender recommender;
    private final RouteFinder routeFinder;

    private final RecommendationMapper recommendationMapper;

    public RecommendationsResponse recommendLocation(final RecommendationRequest request) {
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getCategoryNames();

        final List<Place> startingPlaces = recommender.findPlacesByNames(request.startingPlaceNames());
        final Map<Place, String> generatedPlacesWithReason = recommender.recommendLocations(
                request.startingPlaceNames(),
                requirement
        );
        final List<Place> generatedPlaces = generatedPlacesWithReason.keySet().stream().toList();

        final Map<Place, Routes> placeRoutes = findRoutesForAll(startingPlaces, generatedPlaces);
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

    private Map<Place, Routes> findRoutesForAll(final List<Place> startingPlaces, final List<Place> generatedPlaces) {
        final Map<Place, Routes> placeRoutes = new HashMap<>();
        for (Place generatedPlace : generatedPlaces) {
            final List<Route> routes = new ArrayList<>();
            for (Place startingPlace : startingPlaces) {
                routes.add(routeFinder.findRoute(startingPlace, generatedPlace));
            }
            placeRoutes.put(generatedPlace, new Routes(routes));
        }
        return placeRoutes;
    }

    private void removePlacesBeyondRange(
            final Map<Place, Routes> placeRoutes,
            final Map<Place, String> generatedPlaces
    ) {
        placeRoutes.forEach((key, value) -> {
            if (!value.isAcceptable()) {
                generatedPlaces.remove(key);
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
