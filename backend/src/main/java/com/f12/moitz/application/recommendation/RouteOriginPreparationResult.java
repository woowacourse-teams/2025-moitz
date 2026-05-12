package com.f12.moitz.application.recommendation;

import com.f12.moitz.domain.route.RouteOrigins;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;

public class RouteOriginPreparationResult {

    private final List<SubwayStation> originStations;
    private final RouteOrigins routeOrigins;

    public RouteOriginPreparationResult(
            final List<SubwayStation> originStations,
            final RouteOrigins routeOrigins
    ) {
        this.originStations = List.copyOf(originStations);
        this.routeOrigins = routeOrigins;
    }

    public List<SubwayStation> getOriginStations() {
        return originStations;
    }

    public RouteOrigins getRouteOrigins() {
        return routeOrigins;
    }

}
