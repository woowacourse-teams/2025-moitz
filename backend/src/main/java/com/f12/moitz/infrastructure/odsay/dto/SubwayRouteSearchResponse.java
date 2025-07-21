package com.f12.moitz.infrastructure.odsay.dto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record SubwayRouteSearchResponse(
        ResultResponse result,
        Optional<ErrorResponse> error
) {

    public int getLeastTime() {
        return result.path().stream()
                .min((Comparator.comparingInt(path -> path.info().totalTime())))
                .map(path -> path.info().totalTime())
                .orElseThrow(() -> new IllegalStateException("이동 가능 경로가 존재하지 않습니다."));
    }

    public List<Integer> getTimes() {
        return result.path().stream()
                .map(path -> path.info().totalTime())
                .toList();
    }

}

record ResultResponse(
        int subwayCount,
        List<PathResponse> path
) {

}

record PathResponse(
        int pathType,
        InfoResponse info,
        List<SubPathResponse> subPath
) {

}

record InfoResponse(
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

record SubPathResponse(
        int trafficType,
        int sectionTime,
        int intervalTime,
        String startName,
        double startX,
        double startY,
        String endName,
        double endX,
        double endY,
        List<LaneResponse> lane,
        String way,
        String startExitNo,
        Double startExitX,
        Double startExitY,
        String endExitNo,
        Double endExitX,
        Double endExitY
) {

}

record LaneResponse(
        String name,
        Integer subwayCode
) {

}

record ErrorResponse(
        String code,
        String msg
) {

}
