package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "추천 결과 응답")
public record RecommendationsResponse(
        @Schema(description = "출발지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<StartingPlaceResponse> startingPlaces,
        @Schema(description = "추천 지역 정보 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<RecommendationResponse> locations
) {

}
