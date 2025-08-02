package com.f12.moitz.infrastructure.odsay.dto;

import java.util.List;

public record SubPathResponse(
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
