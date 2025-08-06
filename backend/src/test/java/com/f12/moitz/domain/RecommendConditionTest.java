package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.common.error.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendConditionTest {

    @Test
    @DisplayName("title로부터 RecommendCondition을 올바르게 생성한다")
    void fromTitle() {
        // Given
        final String chatTitle = "CHAT";

        // When
        final RecommendCondition recommendCondition = RecommendCondition.fromTitle(chatTitle);

        // Then
        assertThat(recommendCondition).isEqualTo(RecommendCondition.CHAT);
    }

    @Test
    @DisplayName("존재하지 않는 title이라면 예외를 발생시킨다")
    void fromTitleWithInvalidTitle() {
        // Given
        final String invalidTitle = "INVALID_TITLE";

        // When & Then
        assertThatThrownBy(() -> RecommendCondition.fromTitle(invalidTitle))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("유효하지 않은 성격입니다.");
    }
}
