package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathTest {

    @Test
    @DisplayName("예외가 발생하지 않고 경로가 생성된다")
    void doesNotThrow() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final TravelMethod travelMethod = TravelMethod.SUBWAY;
        final int travelTime = 10;
        final String subwayLineName = "2호선";

        // When & Then
        assertThatNoException().isThrownBy(() -> new Path(start, end, travelMethod, travelTime, subwayLineName));
    }

    @Test
    @DisplayName("필수 인자가 null이라면 경로를 생성할 수 없다")
    void isThrownByNullArguments() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final TravelMethod travelMethod = TravelMethod.SUBWAY;
        final int travelTime = 10;
        final String subwayLineName = "2호선";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Path(null, end, travelMethod, travelTime, subwayLineName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 장소와 끝 장소는 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, null, travelMethod, travelTime, subwayLineName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 장소와 끝 장소는 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, end, null, travelTime, subwayLineName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 수단은 필수입니다.");
        });
    }

    @Test
    @DisplayName("이동 시간이 음수라면 경로를 생성할 수 없다")
    void isThrownByNegativeTravelTime() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final TravelMethod travelMethod = TravelMethod.SUBWAY;
        final int travelTime = -10;
        final String subwayLineName = "2호선";

        // When & Then
        assertThatThrownBy(() -> new Path(start, end, travelMethod, travelTime, subwayLineName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이동 시간은 null이거나 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("이동 수단에 따른 시작 장소와 끝 장소의 유효성을 검사한다")
    void validateStartToEnd() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final TravelMethod subway = TravelMethod.SUBWAY;
        final TravelMethod transfer = TravelMethod.TRANSFER;
        final int travelTime = 10;
        final String subwayLineName = "2호선";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Path(start, start, subway, travelTime, subwayLineName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 장소와 끝 장소는 같을 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, end, transfer, travelTime, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("환승 통로 이동에서의 시작과 끝 장소는 다를 수 없습니다..");
        });
    }

    @Test
    @DisplayName("이동 수단에 따른 지하철 노선명의 유효성을 검사한다")
    void validateSubwayLineName() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final TravelMethod bus = TravelMethod.BUS;
        final TravelMethod subway = TravelMethod.SUBWAY;
        final int travelTime = 10;
        final String subwayLineName = "2호선";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Path(start, end, bus, travelTime, subwayLineName))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("지하철이 아니라면 호선 정보를 지닐 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, end, subway, travelTime, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("지하철인 경우 지하철 노선명이 필수입니다.");
        });
    }
}
