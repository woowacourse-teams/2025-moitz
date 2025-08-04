package com.f12.moitz.application.dto;

public record PathResponse(
        int index,
        String startStation,
        double startX,
        double startY,
        String endStation,
        double endX,
        double endY,
        String lineCode,
        int travelTime
) {

}
