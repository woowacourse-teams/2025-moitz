package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.OriginDestinations;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.recommendation.RecommendedCandidateTravels;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.route.RouteOrigins;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RecommendedCandidateRouteService {

    private final RouteFinder routeFinder;

    public RecommendedCandidateRouteService(
            @Qualifier("subwayRouteFinderAdapter") final RouteFinder routeFinder
    ) {
        this.routeFinder = routeFinder;
    }

    public RecommendedCandidateTravels prepare(
            final RouteOrigins routeOrigins,
            final RecommendedCandidates recommendedCandidates
    ) {
        final List<Place> recommendedCandidatePlaces = recommendedCandidates.getPlaces();
        final OriginDestinations originDestinations = routeOrigins.createOriginDestinationsTo(recommendedCandidatePlaces);
        return new RecommendedCandidateTravels(findCandidateRoutesByDestination(originDestinations));
    }

    private Map<Place, List<CandidateRoute>> findCandidateRoutesByDestination(
            final OriginDestinations originDestinations
    ) {
        final List<CandidateRoute> candidateRoutes = routeFinder.findCandidateRoutes(originDestinations.getValues());
        return originDestinations.groupCandidateRoutesByDestination(candidateRoutes);
    }

}
