package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteCandidate;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouteCandidatePreparationService {

    private final SubwayStationService subwayStationService;
    private final RouteFinder routeFinder;

    public RouteCandidatePreparationService(
            final SubwayStationService subwayStationService,
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder
    ) {
        this.subwayStationService = subwayStationService;
        this.routeFinder = routeFinder;
    }

    public RouteCandidatePreparationResult prepare(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        final List<Place> candidatePlaces = getCandidatePlaces(originStations, routeOrigins, dispersionPolicy);
        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsTo(candidatePlaces);
        final Map<Place, Routes> candidateRoutes = findRoutesByDestination(originDestinations);
        final List<RouteCandidate> routeCandidates = toRouteCandidates(candidatePlaces, candidateRoutes);
        return new RouteCandidatePreparationResult(candidatePlaces, candidateRoutes, routeCandidates);
    }

    private List<Place> getCandidatePlaces(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        final int radiusKilometers = dispersionPolicy.candidateSearchRadiusKilometers();
        final List<String> originNames = routeOrigins.getNames();
        final List<Place> candidatePlaces = subwayStationService.generateCandidatePlace(
                        originStations,
                        radiusKilometers
                ).stream()
                .filter(place -> !originNames.contains(place.getName()))
                .map(Place.class::cast)
                .toList();

        log.debug(
                "후보역 1차 필터 완료 - policy={}, radius={}km, 후보 {}개",
                dispersionPolicy,
                radiusKilometers,
                candidatePlaces.size()
        );
        return candidatePlaces;
    }

    private Map<Place, Routes> findRoutesByDestination(final List<OriginDestination> originDestinations) {
        final List<Route> routes = routeFinder.findRoutes(originDestinations);
        return collectByDestination(originDestinations, routes);
    }

    private Map<Place, Routes> collectByDestination(
            final List<OriginDestination> originDestinations,
            final List<Route> routes
    ) {
        return IntStream.range(0, originDestinations.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        index -> originDestinations.get(index).getDestination(),
                        Collectors.mapping(
                                routes::get,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> new Routes(entry.getValue())
                ));
    }

    private List<RouteCandidate> toRouteCandidates(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        return candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .map(place -> new RouteCandidate(place, candidateRoutes.get(place)))
                .toList();
    }

}
