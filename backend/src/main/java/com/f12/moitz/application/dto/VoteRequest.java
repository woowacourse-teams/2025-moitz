package com.f12.moitz.application.dto;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 요청")
public record VoteRequest(
        @Schema(description = "투표할 추천 지역", example = "강변역", requiredMode = Schema.RequiredMode.REQUIRED)
        String locationName
) {

    public VoteRequest {
        validate(locationName);
    }

    private void validate(final String locationName) {
        if (locationName.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_CANDIDATE_NAME, locationName);
        }
    }

}
