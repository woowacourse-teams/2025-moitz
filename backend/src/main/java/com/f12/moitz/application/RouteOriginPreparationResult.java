package com.f12.moitz.application;

import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;

public class RouteOriginPreparationResult {

    private final List<SubwayStation> startingPlaces;
    private final RouteOrigins routeOrigins;

    public RouteOriginPreparationResult(
            final List<SubwayStation> startingPlaces,
            final RouteOrigins routeOrigins
    ) {
        this.startingPlaces = List.copyOf(startingPlaces);
        this.routeOrigins = routeOrigins;
    }

    public List<SubwayStation> getStartingPlaces() {
        return startingPlaces;
    }

    public RouteOrigins getRouteOrigins() {
        return routeOrigins;
    }

}
