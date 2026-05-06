package com.f12.moitz.application.port.place;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import java.util.List;
import java.util.Objects;

public record PlaceRecommendationCriteria(
        List<RecommendCondition> conditions,
        int limitPerCondition
) {

    public PlaceRecommendationCriteria {
        if (conditions == null || conditions.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_RECOMMEND_CONDITION, conditions);
        }
        if (conditions.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_RECOMMEND_CONDITION, conditions);
        }
        // Programmer invariant: limitPerCondition is provided by application policy, not user input.
        if (limitPerCondition < 1) {
            throw new IllegalArgumentException("추천 장소 개수는 1개 이상이어야 합니다.");
        }
        conditions = List.copyOf(conditions);
    }

}
