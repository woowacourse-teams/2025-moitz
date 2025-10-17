package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지역별 투표 조회")
public record VotesResponse(
        @Schema(description = "추천 지역", requiredMode = Schema.RequiredMode.REQUIRED)
        String locationName,
        @Schema(description = "투표 개수", requiredMode = Schema.RequiredMode.REQUIRED)
        int count
) {

}
