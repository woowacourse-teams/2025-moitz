package com.f12.moitz.application.adapter;

import com.f12.moitz.application.PlaceService;
import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.subway.SubwayMapPathFinder;
import com.f12.moitz.domain.subway.SubwayPath;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubwayRouteFinderAdapter implements RouteFinder {

    private final SubwayMapPathFinder subwayMapPathFinder;
    private final PlaceService placeService;

    @Override
    public List<Route> findRoutes(final List<StartEndPair> placePairs) {
        return placePairs.stream()
                .map(pair -> {
                    final String startPlaceName = pair.start().getName();
                    final String endPlaceName = pair.end().getName();
                    return new Route(
                            convertPath(subwayMapPathFinder.findShortestTimePath(startPlaceName, endPlaceName))
                    );
                })
                .toList();
    }

    private List<Path> convertPath(final List<SubwayPath> subwayPaths) {
        return subwayPaths.stream()
                .map(subwayPath -> new Path(
                        placeService.findByName(subwayPath.fromName()),
                        placeService.findByName(subwayPath.toName()),
                        subwayPath.travelMethod(),
                        subwayPath.totalTime(),
                        subwayPath.lineName()
                ))
                .toList();
    }

}
