package com.f12.moitz.application.dto;

public record PathResponse(
        int index,
        String startStation,
        double startingX,
        double startingY,
        String endStation,
        double endingX,
        double endingY,
        String lineCode,
        int travelTime
) {

}
