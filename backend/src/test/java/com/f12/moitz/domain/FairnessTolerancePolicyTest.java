package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FairnessTolerancePolicyTest {

    private final FairnessTolerancePolicy fairnessTolerancePolicy = new FairnessTolerancePolicy();

    @Test
    @DisplayName("평균 이동 시간 구간에 따라 공평 허용 시간을 계산한다")
    void resolveToleranceMinutes() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(fairnessTolerancePolicy.resolveToleranceMinutes(score(10, 0)))
                    .isEqualTo(5);
            softAssertions.assertThat(fairnessTolerancePolicy.resolveToleranceMinutes(score(30, 0)))
                    .isEqualTo(9);
            softAssertions.assertThat(fairnessTolerancePolicy.resolveToleranceMinutes(score(31, 0)))
                    .isEqualTo(7);
        });
    }

    @Test
    @DisplayName("이동 시간 차이가 허용 시간 이내면 공평 후보로 허용한다")
    void isTolerable() {
        assertThat(fairnessTolerancePolicy.isTolerable(score(10, 5))).isTrue();
        assertThat(fairnessTolerancePolicy.isTolerable(score(10, 6))).isFalse();
    }

    @Test
    @DisplayName("공평 점수가 null이면 허용 여부를 판단할 수 없다")
    void throwsExceptionWhenScoreIsNull() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> fairnessTolerancePolicy.resolveToleranceMinutes(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("공평 점수는 필수입니다.");
            softAssertions.assertThatThrownBy(() -> fairnessTolerancePolicy.isTolerable(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("공평 점수는 필수입니다.");
        });
    }

    private FairnessScore score(final int averageTravelTime, final int timeDiff) {
        return new FairnessScore(
                averageTravelTime + timeDiff,
                0,
                0,
                timeDiff,
                averageTravelTime
        );
    }

}
