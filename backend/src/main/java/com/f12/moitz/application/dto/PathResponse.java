package com.f12.moitz.application.dto;

public record PathResponse(
        int index,
        String startStation,
        String endStation,
        String lineCode,
        int travelTime
) {

}
