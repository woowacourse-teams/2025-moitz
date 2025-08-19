package com.f12.moitz.application.adapter;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.subway.SubwayMapPathFinder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubwayRouteFinderAdapter implements RouteFinder {

    private final SubwayMapPathFinder subwayMapPathFinder;

    @Override
    public List<Route> findRoutes(final List<StartEndPair> placePairs) {
        final List<Route> routes = placePairs.stream()
                .map(pair -> {
                    final String startPlaceName = getStationName(pair.start().getName());
                    final String endPlaceName = getStationName(pair.end().getName());
                    return new Route(subwayMapPathFinder.findShortestTimePath(startPlaceName, endPlaceName));
                })
                .toList();

        return routes;
    }

    private String getStationName(final String stationName) {
        if (!stationName.equals("서울역") && stationName.endsWith("역")) {
            return stationName.substring(0, stationName.length() - 1);
        }
        return stationName;
    }

}
