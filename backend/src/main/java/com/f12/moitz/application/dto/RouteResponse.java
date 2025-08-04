package com.f12.moitz.application.dto;

import java.util.List;

public record RouteResponse(
        String startPlace,
        double startingX,
        double startingY,
        int transferCount,
        int totalTravelTime,
        List<PathResponse> paths
) {

}
