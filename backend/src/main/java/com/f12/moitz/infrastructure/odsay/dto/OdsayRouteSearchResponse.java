package com.f12.moitz.infrastructure.odsay.dto;

import java.util.List;

public record OdsayRouteSearchResponse(
        Result result
) {

}

record Station(
        int index,
        int stationID,
        String stationName,
        double x,
        double y,
        String stationNameKor,
        String stationNameJpnKata,
        Integer stationCityCode,
        Integer stationProviderCode,
        String localStationID,
        String arsID,
        String isNonStop
) {

}

record PassStopList(
        List<Station> stations
) {

}

record Lane(
        String name,
        Integer subwayCode,
        Integer subwayCityCode,
        String busNo,
        Integer type,
        Integer busID,
        String busLocalBlID,
        Integer busCityCode,
        Integer busProviderCode,
        String nameKor,
        String nameJpnKata,
        String busNoKor,
        String busNoJpnKata
) {

}

record SubPath(
        int trafficType,
        double distance,
        int sectionTime,
        int intervalTime,
        String startName,
        double startX,
        double startY,
        String endName,
        double endX,
        double endY,
        int startID,
        int endID,
        Integer stationCount,
        List<Lane> lane,
        PassStopList passStopList,
        String way,
        Integer wayCode,
        String door,
        String startNameKor,
        String startNameJpnKata,
        String endNameKor,
        String endNameJpnKata,
        Integer startStationCityCode,
        Integer startStationProviderCode,
        String startLocalStationID,
        String startArsID,
        Integer endStationCityCode,
        Integer endStationProviderCode,
        String endLocalStationID,
        String endArsID,
        String startExitNo,
        Double startExitX,
        Double startExitY,
        String endExitNo,
        Double endExitX,
        Double endExitY
) {

}

record Info(
        double trafficDistance,
        int totalWalk,
        int totalTime,
        int payment,
        int busTransitCount,
        int subwayTransitCount,
        String mapObj,
        String firstStartStation,
        String lastEndStation,
        int totalStationCount,
        int busStationCount,
        int subwayStationCount,
        double totalDistance,
        Integer checkIntervalTime,
        String checkIntervalTimeOverYnString,
        Integer totalIntervalTime,
        Integer totalWalkTime
) {

}

record Path(
        int pathType,
        Info info,
        List<SubPath> subPath
) {

}

record Result(
        int searchType,
        int outTrafficCheck,
        int busCount,
        int subwayCount,
        int subwayBusCount,
        double pointDistance,
        int startRadius,
        int endRadius,
        List<Path> path
) {

}
