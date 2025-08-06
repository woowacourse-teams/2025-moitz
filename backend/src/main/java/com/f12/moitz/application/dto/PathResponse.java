package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세부 이동 경로")
public record PathResponse(
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "이동 출발 지점", example = "강변역", requiredMode = Schema.RequiredMode.REQUIRED)
        String startStation,
        @Schema(description = "출발 지점 경도(X)", example = "126.9815", requiredMode = Schema.RequiredMode.REQUIRED)
        double startingX,
        @Schema(description = "출발 지점 위도(Y)", example = "37.4765", requiredMode = Schema.RequiredMode.REQUIRED)
        double startingY,
        @Schema(description = "이동 도착 지점", example = "잠실역", requiredMode = Schema.RequiredMode.REQUIRED)
        String endStation,
        @Schema(description = "도착 지점 경도(X)", example = "126.9815", requiredMode = Schema.RequiredMode.REQUIRED)
        double endingX,
        @Schema(description = "도착 지점 위도(Y)", example = "37.4765", requiredMode = Schema.RequiredMode.REQUIRED)
        double endingY,
        @Schema(description = "지하철 호선명", example = "2호선", requiredMode = Schema.RequiredMode.REQUIRED)
        String lineCode,
        @Schema(description = "이동 시간", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int travelTime
) {

}
