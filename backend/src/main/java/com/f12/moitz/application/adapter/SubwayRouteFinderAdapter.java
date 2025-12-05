package com.f12.moitz.application.adapter;

import com.f12.moitz.application.SubwayStationService;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Course;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.subway.StationSequence;
import com.f12.moitz.domain.subway.SubwayPath;
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
    public List<Route> findRoutes(final List<StartEndPair> placePairs) {
        return findStationSequences(placePairs).stream()
                .map(sequence -> new Route(convertPath(sequence.groupByLine())))
                .toList();
    }

    @Override
    public List<Course> findCourses(final List<StartEndPair> placePairs) {
        return findStationSequences(placePairs).stream()
                .map(sequence -> new Course(sequence.getPoints()))
                .toList();
    }

    private List<StationSequence> findStationSequences(final List<StartEndPair> placePairs) {
        return placePairs.stream()
                .map(this::findStationSequence)
                .toList();
    }

    private StationSequence findStationSequence(final StartEndPair pair) {
        final SubwayStation startStation = subwayStationService.getByName(pair.start().getName());
        final SubwayStation endStation = subwayStationService.getByName(pair.end().getName());
        return subwayRouteCalculator.findShortestTimePath(startStation, endStation);
    }

    private List<Path> convertPath(final List<SubwayPath> subwayPaths) {
        return subwayPaths.stream()
                // TODO: SubwayPath, Path 두 객체의 필드가 동일한데, 둘을 통합할 수 있을지 고민하기
                .map(subwayPath -> new Path(
                        subwayPath.from(),
                        subwayPath.to(),
                        subwayPath.travelMethod(),
                        subwayPath.totalTime(),
                        subwayPath.line()
                ))
                .toList();
    }

}
