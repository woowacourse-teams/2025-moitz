package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PointTest {

    @Test
    @DisplayName("예외가 발생하지 않고 좌표가 생성된다")
    void doesNotThrow() {
        // Given
        final double x = 125.127;
        final double y = 34.578;

        // When & Then
        assertThatNoException().isThrownBy(() -> new Point(x, y));
    }

    @Test
    @DisplayName("X(경도) 값이 대한민국 영역 밖이라면 좌표를 생성할 수 없다.")
    void isThrownByInvalidXValue() {
        // Given
        final double smallX = 122.127;
        final double bigX = 132.127;
        final double y = 34.578;

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Point(smallX, y))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("X(경도)는 대한민국 영역 내에 있어야 합니다. 범위: 124 ~ 132");

            softAssertions.assertThatThrownBy(() -> new Point(bigX, y))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("X(경도)는 대한민국 영역 내에 있어야 합니다. 범위: 124 ~ 132");
        });
    }

    @Test
    @DisplayName("Y(위도) 값이 대한민국 영역 밖이라면 좌표를 생성할 수 없다.")
    void isThrownByInvalidYValue() {
        // Given
        final double x = 125.127;
        final double smallY = 32.578;
        final double bigY = 43.578;

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Point(x, smallY))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Y(위도)는 대한민국 영역 내에 있어야 합니다. 범위: 33 ~ 43");
            softAssertions.assertThatThrownBy(() -> new Point(x, bigY))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Y(위도)는 대한민국 영역 내에 있어야 합니다. 범위: 33 ~ 43");
        });
    }

}
