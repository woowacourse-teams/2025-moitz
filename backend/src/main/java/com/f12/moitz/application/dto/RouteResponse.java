package com.f12.moitz.application.dto;

import java.util.List;

public record RouteResponse(
        String startPlace,
        String startingX,
        String startingY,
        String transferCount,
        int totalTravelTime,
        List<PathResponse> paths
) {

}
