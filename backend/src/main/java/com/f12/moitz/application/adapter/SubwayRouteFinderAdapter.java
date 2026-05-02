package com.f12.moitz.application.adapter;

import com.f12.moitz.application.SubwayStationService;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.OriginDestination;
import com.f12.moitz.domain.subway.StationSequence;
import com.f12.moitz.domain.subway.SubwayRouteCalculator;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubwayRouteFinderAdapter implements RouteFinder {

    private final SubwayStationService subwayStationService;
    private final SubwayRouteCalculator subwayRouteCalculator;

    public SubwayRouteFinderAdapter(
            final SubwayStationService subwayStationService,
            final SubwayRouteCalculator subwayRouteCalculator
    ) {
        this.subwayStationService = subwayStationService;
        this.subwayRouteCalculator = subwayRouteCalculator;
    }

    @Override
    public List<Route> findRoutes(final List<OriginDestination> originDestinations) {
        return findStationSequences(originDestinations).stream()
                .map(sequence -> new Route(sequence.groupByLine()))
                .toList();
    }

    @Override
    public List<Course> findCourses(final List<OriginDestination> originDestinations) {
        return findStationSequences(originDestinations).stream()
                .map(sequence -> new Course(sequence.getPoints()))
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
