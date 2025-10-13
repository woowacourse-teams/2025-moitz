package com.f12.moitz.domain.subway;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EdgeTest {

    private SubwayStation destination;

    @BeforeEach
    void setUp() {
        destination = new SubwayStation("강남역", new Point(127.0276, 37.4979));
    }

    @Test
    @DisplayName("예외가 발생하지 않고 간선이 생성된다")
    void doesNotThrow() {
        // Given
        final int timeInSeconds = 180;
        final int distance = 1000;
        final String subwayLine = "2호선";

        // When & Then
        assertThatNoException().isThrownBy(() -> new Edge(destination, timeInSeconds, distance, subwayLine));
    }

    @Test
    @DisplayName("필수 인자가 null이거나 유효하지 않다면 간선을 생성할 수 없다")
    void isThrownByInvalidArguments() {
        // Given
        final int timeInSeconds = 180;
        final int distance = 1000;
        final String subwayLine = "2호선";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Edge(null, timeInSeconds, distance, subwayLine))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("목적지는 비어있을 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Edge(destination, -1, distance, subwayLine))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이동 시간은 음수일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Edge(destination, timeInSeconds, -1, subwayLine))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이동 거리는 음수일 수 없습니다.");
        });
    }

}
