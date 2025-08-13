package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "경로 정보")
public record RouteResponse(
        @Schema(description = "출발지 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        long startingPlaceId,
        @Schema(description = "환승 횟수", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int transferCount,
        @Schema(description = "총 이동 시간", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
        int totalTravelTime,
        @Schema(description = "세부 경로", requiredMode = Schema.RequiredMode.REQUIRED)
        List<PathResponse> paths
) {

}
