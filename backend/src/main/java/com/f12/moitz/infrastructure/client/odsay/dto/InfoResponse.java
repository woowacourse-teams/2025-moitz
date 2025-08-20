package com.f12.moitz.infrastructure.client.odsay.dto;

public record InfoResponse(
        double trafficDistance,
        int totalWalk,
        int totalTime,
        int payment,
        int subwayTransitCount, // 환승 횟수
        String mapObj,
        String firstStartStation,
        String lastEndStation,
        int subwayStationCount, // 정류장 수
        Integer totalWalkTime
) {

}
