package com.f12.moitz.application.recommendation;

import com.f12.moitz.application.subway.SubwayStationService;
import com.f12.moitz.application.subway.SubwayRouteService;
import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import com.f12.moitz.domain.recommendation.candidate.RouteCandidate;
import com.f12.moitz.domain.route.OriginDestinations;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.route.RouteOrigins;
import com.f12.moitz.domain.route.Routes;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouteCandidatePreparationService {

    private final SubwayStationService subwayStationService;
    private final SubwayRouteService subwayRouteService;

    public RouteCandidatePreparationService(
            final SubwayStationService subwayStationService,
            final SubwayRouteService subwayRouteService
    ) {
        this.subwayStationService = subwayStationService;
        this.subwayRouteService = subwayRouteService;
    }

    public RouteCandidatePreparationResult prepare(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins,
            final DispersionPolicy dispersionPolicy
    ) {
        final List<Place> candidatePlaces = getCandidatePlaces(originStations, routeOrigins, dispersionPolicy);
        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsTo(candidatePlaces);
        final Map<Place, Routes> candidateRoutes = findRoutesByDestination(originDestinations);
        final List<RouteCandidate> routeCandidates = createRouteCandidates(candidatePlaces, candidateRoutes);
        return new RouteCandidatePreparationResult(candidatePlaces, candidateRoutes, routeCandidates);
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
        final List<Route> routes = subwayRouteService.findRoutes(originDestinations.getValues());
        return originDestinations.groupRoutesByDestination(routes);
    }

    private List<RouteCandidate> createRouteCandidates(
            final List<Place> candidatePlaces,
            final Map<Place, Routes> candidateRoutes
    ) {
        return candidatePlaces.stream()
                .filter(candidateRoutes::containsKey)
                .map(place -> new RouteCandidate(place, candidateRoutes.get(place)))
                .toList();
    }

}
