package com.f12.moitz.application.port.place;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlaceRecommendationCriteriaTest {

    @Test
    void create_ThrowsBadRequestExceptionWhenConditionsAreEmpty() {
        assertThatThrownBy(() -> new PlaceRecommendationCriteria(List.of(), 6))
                .isInstanceOfSatisfying(BadRequestException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_RECOMMEND_CONDITION)
                );
    }

    @Test
    void create_ThrowsBadRequestExceptionWhenConditionsContainNull() {
        assertThatThrownBy(() -> new PlaceRecommendationCriteria(
                Arrays.asList(RecommendCondition.CAFE, null),
                6
        ))
                .isInstanceOfSatisfying(BadRequestException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_RECOMMEND_CONDITION)
                );
    }

    @Test
    void create_ThrowsIllegalArgumentExceptionWhenLimitPerConditionIsInvalid() {
        assertThatThrownBy(() -> new PlaceRecommendationCriteria(List.of(RecommendCondition.CAFE), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 장소 개수는 1개 이상이어야 합니다.");
    }

}
