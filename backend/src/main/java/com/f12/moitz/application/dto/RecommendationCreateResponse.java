package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추천 생성 응답")
public record RecommendationCreateResponse(
        @Schema(description = "추천 데이터 식별자", example = "GFES2E1", requiredMode = Schema.RequiredMode.REQUIRED)
        String id
) {

}
