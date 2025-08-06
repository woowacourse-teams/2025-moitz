package com.f12.moitz.application;

import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.port.LocationRecommender;
import com.f12.moitz.application.port.PlaceRecommender;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LocationRecommender locationRecommender;
    private final PlaceRecommender placeRecommender;
    private final RouteFinder routeFinder;

    private final RecommendationMapper recommendationMapper;

    public List<RecommendationResponse> recommendLocation(final RecommendationRequest request) {
        final String requirement = RecommendCondition.fromTitle(request.requirement()).getCategoryNames();

        final List<Place> startingPlaces = placeRecommender.findPlacesByNames(request.startingPlaceNames());
        final Map<Place, String> generatedPlaces = locationRecommender.recommendLocations(
                request.startingPlaceNames(),
                requirement
        );
        final Map<Place, Routes> placeRoutes = findRoutesForAll(new HashSet<>(startingPlaces), generatedPlaces.keySet());

        placeRoutes.forEach((key, value) -> {
            if (!value.isAcceptable()) {
                generatedPlaces.remove(key);
            }
        });

        final Map<Place, List<RecommendedPlace>> recommendedPlaces = placeRecommender.recommendPlaces(
                new HashSet<>(generatedPlaces.keySet()),
                requirement
        );

        final Recommendation recommendation = toRecommendation(generatedPlaces, recommendedPlaces, placeRoutes);

        return recommendationMapper.toResponse(recommendation, generatedPlaces);
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

    private Map<Place, Routes> findRoutesForAll(final Set<Place> startingPlaces, final Set<Place> generatedPlaces) {
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

}
