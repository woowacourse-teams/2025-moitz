package com.f12.moitz.domain.subway;

import com.f12.moitz.domain.TravelMethod;

public record SubwayPath(
        String fromName,
        String toName,
        TravelMethod travelMethod,
        int totalTime,
        String lineName
) {

}
