package com.f12.moitz.domain.recommendation.candidate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransferBurdenTest {

    @Test
    @DisplayName("평균 환승 횟수를 가장 우선해 환승 부담을 비교한다")
    void compareTo_PrioritizesAverageTransferCount() {
        final TransferBurden lowerAverage = new TransferBurden(0.5, 2, 2);
        final TransferBurden higherAverage = new TransferBurden(1.0, 1, 0);

        assertThat(lowerAverage).isLessThan(higherAverage);
    }

    @Test
    @DisplayName("평균 환승 횟수가 같으면 최대 환승 횟수와 환승 횟수 차이로 비교한다")
    void compareTo_UsesMaxTransferCountAndTransferDiffAsTieBreakers() {
        final TransferBurden lowerMaxTransfer = new TransferBurden(1.0, 1, 1);
        final TransferBurden higherMaxTransfer = new TransferBurden(1.0, 2, 0);
        final TransferBurden lowerTransferDiff = new TransferBurden(1.0, 2, 1);
        final TransferBurden higherTransferDiff = new TransferBurden(1.0, 2, 2);

        assertThat(lowerMaxTransfer).isLessThan(higherMaxTransfer);
        assertThat(lowerTransferDiff).isLessThan(higherTransferDiff);
    }

    @Test
    @DisplayName("환승 부담은 음수 값을 가질 수 없다")
    void constructor_ThrowsExceptionWhenArgumentsAreInvalid() {
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new TransferBurden(-1.0, 0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("평균 환승 횟수는 음수이거나 NaN일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new TransferBurden(Double.NaN, 0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("평균 환승 횟수는 음수이거나 NaN일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new TransferBurden(0.0, -1, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("최대 환승 횟수는 음수일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new TransferBurden(0.0, 0, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("환승 횟수 차이는 음수일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("공평 점수에서 환승 부담을 조회한다")
    void getTransferBurdenFromFairnessScore() {
        final FairnessScore fairnessScore = new FairnessScore(
                30,
                2,
                0.5,
                2,
                10,
                25,
                5,
                5
        );

        assertThat(fairnessScore.getTransferBurden())
                .isEqualTo(new TransferBurden(0.5, 2, 2));
    }

}
