package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출발지 정보")
public record StartingPlaceResponse(
        @Schema(description = "ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        long id,
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "출발지 경도(X)", example = "126.952", requiredMode = Schema.RequiredMode.REQUIRED)
        double x,
        @Schema(description = "출발지 위도(Y)", example = "37.481", requiredMode = Schema.RequiredMode.REQUIRED)
        double y,
        @Schema(description = "출발지 이름", example = "잠실역", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {
}
