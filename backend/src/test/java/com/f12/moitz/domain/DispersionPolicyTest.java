package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DispersionPolicyTest {

    @Test
    @DisplayName("출발지 분산도에 따라 정책 tier를 판정한다")
    void resolve() {
        assertThat(DispersionPolicy.resolve(3, 45, 30.0, 0))
                .isEqualTo(DispersionPolicy.TIER_1);
        assertThat(DispersionPolicy.resolve(4, 70, 45.0, 0))
                .isEqualTo(DispersionPolicy.TIER_2);
        assertThat(DispersionPolicy.resolve(6, 110, 75.0, 3))
                .isEqualTo(DispersionPolicy.TIER_3);
        assertThat(DispersionPolicy.resolve(6, 140, 90.0, 5))
                .isEqualTo(DispersionPolicy.TIER_4);
        assertThat(DispersionPolicy.resolve(6, 141, 90.0, 5))
                .isEqualTo(DispersionPolicy.TIER_5);
    }

    @Test
    @DisplayName("정책 tier별 허용 기준을 적용한다")
    void isAcceptable() {
        final FairnessScore outerRangeScore = new FairnessScore(95, 3, 2, 68, 70);

        assertThat(DispersionPolicy.TIER_4.isAcceptable(outerRangeScore)).isFalse();
        assertThat(DispersionPolicy.TIER_5.isAcceptable(outerRangeScore)).isTrue();
    }

    @Test
    @DisplayName("최대 출발지 수를 초과하면 분산도 정책을 판정할 수 없다")
    void resolve_ThrowsException_WhenPartySizeExceedsLimit() {
        assertThatThrownBy(() -> DispersionPolicy.resolve(7, 100, 70.0, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("6개 이하");
    }

}
