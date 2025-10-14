package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.common.error.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendConditionTest {

    @Test
    @DisplayName("title로부터 RecommendCondition을 올바르게 생성한다")
    void fromTitle() {
        // Given
        final List<String> chatTitle = List.of("CAFE", "BAR");

        // When
        final List<RecommendCondition> recommendCondition = RecommendCondition.fromTitle(chatTitle);

        // Then
        assertThat(recommendCondition).hasSize(2);
        assertThat(recommendCondition).containsExactly(RecommendCondition.CAFE,RecommendCondition.BAR);
    }

    @Test
    @DisplayName("존재하지 않는 title이라면 예외를 발생시킨다")
    void fromTitleWithInvalidTitle() {
        // Given
        final List<String> invalidTitle = List.of("INVALID_TITLE");

        // When & Then
        assertThatThrownBy(() -> RecommendCondition.fromTitle(invalidTitle))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("유효하지 않은 성격입니다. " + invalidTitle);
    }

}
