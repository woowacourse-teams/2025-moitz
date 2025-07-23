package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "지역 추천 응답")
public record LocationRecommendResponse(
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "위도", example = "37.49808633653005", requiredMode = Schema.RequiredMode.REQUIRED)
        double y,
        @Schema(description = "경도", example = "127.02800140627488", requiredMode = Schema.RequiredMode.REQUIRED)
        double x,
        @Schema(description = "추천 지역 이름", example = "강남역", requiredMode = Schema.RequiredMode.REQUIRED)
        String placeName,
        @Schema(description = "평균 이동 시간", example = "21", requiredMode = Schema.RequiredMode.REQUIRED)
        int averageTime,
        @Schema(description = "최적의 추천 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isBest,
        @Schema(description = "AI 추천 한 마디", example = "역세권, 편의시설 풍부! \uD83D\uDC4D\uD83D\uDE0B", requiredMode = Schema.RequiredMode.REQUIRED)
        String aiDescription
) {

}
