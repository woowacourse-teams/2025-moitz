package com.f12.moitz.domain.service;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.Location;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.domain.Route;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationDomainService {

    private final RouteFinder routeFinder;

    public List<Location> findAndSort(
            List<Place> startingPlaces,
            Map<Place, String> generatedPlaces,
            Map<Place, List<RecommendedPlace>> placeListMap
    ) {
        Map<Place, List<Route>> placeRoutes = findRoutesForAll(new HashSet<>(startingPlaces), generatedPlaces.keySet());

        List<Location> locations = generatedPlaces.keySet().stream()
                .map(place -> new Location(
                        place,
                        placeRoutes.get(place),
                        placeListMap.get(place)
                ))
                .toList();

        return locations.stream()
                .sorted(Comparator.comparingInt(Location::calculateAverageTravelTime))
                .collect(Collectors.toList());
    }

    private Map<Place, List<Route>> findRoutesForAll(Set<Place> startingPlaces, Set<Place> generatedPlaces) {
        Map<Place, List<Route>> placeRoutes = new HashMap<>();
        for (Place generatedPlace : generatedPlaces) {
            List<Route> routes = new ArrayList<>();
            for (Place startingPlace : startingPlaces) {
                routes.add(routeFinder.findRoute(startingPlace, generatedPlace));
            }
            placeRoutes.put(generatedPlace, routes);
        }
        return placeRoutes;
    }
}
