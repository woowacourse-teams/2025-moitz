package com.f12.moitz.infrastructure.client.gemini.dto;

import java.util.List;

public record RecommendedLocation(
        String locationName,
        List<MovingInfo> movingInfos,
        String additionalConditionsMet
) {

}

record MovingInfo(
        String startStationName,
        List<String> travelMethods,
        String travelRoute,
        int totalTimeInMinutes,
        String travelCost,
        int numberOfTransfers
) {

}
