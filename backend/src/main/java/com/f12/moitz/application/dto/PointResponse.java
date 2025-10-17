package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "x, y 좌표를 가진 지점")
public record PointResponse(
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "x 좌표", example = "126.96974961781686", requiredMode = Schema.RequiredMode.REQUIRED)
        double x,
        @Schema(description = "y 좌표", example = "37.55332892758497", requiredMode = Schema.RequiredMode.REQUIRED)
        double y
) {

}
