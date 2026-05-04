package com.f12.moitz.application.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추천 장소 정보")
public record PlaceRecommendResponse(
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "x 좌표", example = "127.001698", requiredMode = Schema.RequiredMode.REQUIRED)
        double x,
        @Schema(description = "y 좌표", example = "37.567013", requiredMode = Schema.RequiredMode.REQUIRED)
        double y,
        @Schema(description = "이름", example = "매머드커피 루터회관점", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "카테고리", example = "카페", requiredMode = Schema.RequiredMode.REQUIRED)
        String category,
        @Schema(description = "도보 이동 시간", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int walkingTime,
        @Schema(description = "장소 정보 URL", example = "http://place.map.kakao.com/35026031", requiredMode = Schema.RequiredMode.REQUIRED)
        String placeUrl,
        @Schema(description = "장소 이미지 URL", example = "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966", requiredMode = Schema.RequiredMode.REQUIRED)
        String imageUrl
) {

}
