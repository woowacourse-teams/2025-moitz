package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 요청")
public record VoteRequest(
        @Schema(description = "투표할 추천 지역", example = "강변역", requiredMode = Schema.RequiredMode.REQUIRED)
        String location
) {

}
