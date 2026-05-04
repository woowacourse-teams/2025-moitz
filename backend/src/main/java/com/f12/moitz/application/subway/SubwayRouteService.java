package com.f12.moitz.application.subway;

import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.OriginDestination;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.route.StationSequence;
import com.f12.moitz.domain.subway.route.SubwayRouteCalculator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubwayRouteService {

    private final SubwayStationService subwayStationService;
    private final SubwayRouteCalculator subwayRouteCalculator;

    public SubwayRouteService(
            final SubwayStationService subwayStationService,
            final SubwayRouteCalculator subwayRouteCalculator
    ) {
        this.subwayStationService = subwayStationService;
        this.subwayRouteCalculator = subwayRouteCalculator;
    }

    public List<Route> findRoutes(final List<OriginDestination> originDestinations) {
        return findStationSequences(originDestinations).stream()
                .map(StationSequence::toRoute)
                .toList();
    }

    public List<CandidateRoute> findCandidateRoutes(final List<OriginDestination> originDestinations) {
        return findStationSequences(originDestinations).stream()
                .map(StationSequence::toCandidateRoute)
                .toList();
    }

    private List<StationSequence> findStationSequences(final List<OriginDestination> originDestinations) {
        return originDestinations.stream()
                .map(this::findStationSequence)
                .toList();
    }

    private StationSequence findStationSequence(final OriginDestination originDestination) {
        final SubwayStation startStation = subwayStationService.getByName(originDestination.getOrigin().getName());
        final SubwayStation endStation = subwayStationService.getByName(
                originDestination.getDestination().getName()
        );
        return subwayRouteCalculator.findShortestTimePath(startStation, endStation);
    }

}
