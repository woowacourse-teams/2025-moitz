package com.f12.moitz.application.port.subway.dto;

public record RawPathInfo(
        RawStationInfo departureStation,
        RawStationInfo arrivalStation,
        int stationSectionDistance,
        boolean isTransfer,
        int waitingSeconds,
        int requiredSeconds,
        String trainDepartureTime
) {

}
