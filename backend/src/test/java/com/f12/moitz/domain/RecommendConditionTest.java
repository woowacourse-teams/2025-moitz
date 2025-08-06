package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 하는 추천 조건이 없습니다.");
    }
}
