package com.f12.moitz.application.dto.legacy;

import com.f12.moitz.application.dto.recommendation.StartingPlaceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "레거시 추천 결과 응답")
public record MockLegacyRecommendationResponse(
        @Schema(description = "추천 조건", requiredMode = Schema.RequiredMode.REQUIRED)
        String requirement,
        @Schema(description = "출발지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<StartingPlaceResponse> startingPlaces,
        @Schema(description = "추천 지역 정보 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<LegacyRecommendationResponse> locations
) {

}
