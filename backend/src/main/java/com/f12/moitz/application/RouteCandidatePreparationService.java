package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.DispersionPolicy;
import com.f12.moitz.domain.OriginDestinations;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
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
        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsTo(candidatePlaces);
        final Map<Place, Routes> candidateRoutes = findRoutesByDestination(originDestinations);
        return new RouteCandidatePreparationResult(candidatePlaces, candidateRoutes);
    }

    private List<Place> getCandidatePlaces(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        final int radiusKilometers = dispersionPolicy.candidateSearchRadiusKilometers();
        final List<SubwayStation> nearbyStations = subwayStationService.generateCandidatePlace(
                originStations,
                radiusKilometers
        );
        final List<Place> candidatePlaces = routeOrigins.excludeOriginsFrom(nearbyStations);

        log.debug(
                "후보역 1차 필터 완료 - policy={}, radius={}km, 후보 {}개",
                dispersionPolicy,
                radiusKilometers,
                candidatePlaces.size()
        );
        return candidatePlaces;
    }

    private Map<Place, Routes> findRoutesByDestination(final OriginDestinations originDestinations) {
        final List<Route> routes = routeFinder.findRoutes(originDestinations.getValues());
        return originDestinations.groupRoutesByDestination(routes);
    }

}
