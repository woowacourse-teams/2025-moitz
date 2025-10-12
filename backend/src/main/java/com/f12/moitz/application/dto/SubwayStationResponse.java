package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전체 경유 지하철역")
public record SubwayStationResponse(
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "이름", example = "서울역", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "x 좌표", example = "126.96974961781686", requiredMode = Schema.RequiredMode.REQUIRED)
        double x,
        @Schema(description = "y 좌표", example = "37.55332892758497", requiredMode = Schema.RequiredMode.REQUIRED)
        double y
) {

}
