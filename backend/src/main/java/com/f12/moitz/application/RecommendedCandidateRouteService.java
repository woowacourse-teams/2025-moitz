package com.f12.moitz.application;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.CandidateRoute;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RouteOrigins;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        final List<OriginDestination> originDestinations = routeOrigins.createOriginDestinationsTo(recommendedCandidatePlaces);
        return new RecommendedCandidateTravels(findCandidateRoutesByDestination(originDestinations));
    }

    private Map<Place, List<CandidateRoute>> findCandidateRoutesByDestination(
            final List<OriginDestination> originDestinations
    ) {
        final List<CandidateRoute> candidateRoutes = routeFinder.findCandidateRoutes(originDestinations);
        validateCandidateRouteCount(originDestinations, candidateRoutes);
        return IntStream.range(0, originDestinations.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        index -> originDestinations.get(index).getDestination(),
                        Collectors.mapping(
                                candidateRoutes::get,
                                Collectors.toList()
                        )
                ));
    }

    private void validateCandidateRouteCount(
            final List<OriginDestination> originDestinations,
            final List<CandidateRoute> candidateRoutes
    ) {
        if (candidateRoutes == null) {
            throw new IllegalStateException("추천 후보 경로 조회 결과가 null입니다.");
        }
        if (originDestinations.size() != candidateRoutes.size()) {
            throw new IllegalStateException(String.format(
                    "추천 후보 경로 조회 결과 개수가 일치하지 않습니다. 요청=%d, 응답=%d",
                    originDestinations.size(),
                    candidateRoutes.size()
            ));
        }
    }

}
