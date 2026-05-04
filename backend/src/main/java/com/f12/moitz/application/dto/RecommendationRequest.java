package com.f12.moitz.application.dto;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.recommendation.candidate.DispersionPolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "지역 추천 요청")
public record RecommendationRequest(
        @Schema(description = "출발지 이름 목록", example = "[\"강변역\", \"동대문역\", \"서울대입구역\"]", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> startingPlaceNames,
        @Schema(description = "카테고리에 따른 조건", example = "[\"CAFE\", \"RESTAURANT\",\"PC_ROOM_KARAOKE\"]")
        List<String> requirements
) {

        public RecommendationRequest {
                validate(startingPlaceNames);
        }

        private void validate(final List<String> startingPlaceNames) {
                if (startingPlaceNames == null || startingPlaceNames.size() < DispersionPolicy.MIN_STARTING_PLACE_COUNT) {
                        throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION, startingPlaceNames);
                }
                if (startingPlaceNames.size() > DispersionPolicy.MAX_STARTING_PLACE_COUNT) {
                        throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION, startingPlaceNames);
                }
                if (startingPlaceNames.stream().anyMatch(name -> name == null || name.isBlank())) {
                        throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_START_LOCATION, startingPlaceNames);
                }
        }

}
