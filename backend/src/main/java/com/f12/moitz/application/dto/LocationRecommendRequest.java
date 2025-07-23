package com.f12.moitz.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "지역 추천 요청")
public record LocationRecommendRequest(
        @Schema(description = "출발지 이름 목록", example = "[\"강변역\", \"동대문역\", \"서울대입구역\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> startingPoint,
        @Schema(description = "도착 예정 시간", example = "14:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime meetingTime,
        @Schema(description = "추가 요청사항", example = "노래방은 있었으면 좋겠어요!", requiredMode = Schema.RequiredMode.REQUIRED)
        String requirement
) {

}
