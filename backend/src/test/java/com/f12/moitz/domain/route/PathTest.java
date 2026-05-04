package com.f12.moitz.domain.route;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.SubwayLine;
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
        assertThatNoException().isThrownBy(() -> new Path(start, end, travelMethod, travelTime, SubwayLine.fromTitle(subwayLineName)));
    }

    @Test
    @DisplayName("지하철 경로와 환승 경로를 생성 의도에 맞게 생성한다")
    void createByPurpose() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final SubwayLine subwayLine = SubwayLine.fromTitle("2호선");

        // When
        final Path subwayPath = Path.subway(start, end, 10, subwayLine);
        final Path transferPath = Path.transfer(start, 5);

        // Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(subwayPath.getTravelMethod()).isEqualTo(TravelMethod.SUBWAY);
            softAssertions.assertThat(subwayPath.getSubwayLine()).isEqualTo(subwayLine);
            softAssertions.assertThat(subwayPath.getLineName()).isEqualTo("2호선");
            softAssertions.assertThat(transferPath.getTravelMethod()).isEqualTo(TravelMethod.TRANSFER);
            softAssertions.assertThat(transferPath.getStart()).isEqualTo(start);
            softAssertions.assertThat(transferPath.getEnd()).isEqualTo(start);
            softAssertions.assertThat(transferPath.getSubwayLine()).isNull();
            softAssertions.assertThat(transferPath.getLineName()).isNull();
        });
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
            softAssertions.assertThatThrownBy(() -> new Path(null, end, travelMethod, travelTime, SubwayLine.fromTitle(subwayLineName)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 장소와 끝 장소는 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, null, travelMethod, travelTime, SubwayLine.fromTitle(subwayLineName)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 장소와 끝 장소는 필수입니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, end, null, travelTime, SubwayLine.fromTitle(subwayLineName)))
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
        assertThatThrownBy(() -> new Path(start, end, travelMethod, travelTime, SubwayLine.fromTitle(subwayLineName)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이동 시간은 null이거나 음수일 수 없습니다.");
    }

    @Test
    @DisplayName("이동 수단에 따른 시작 장소와 끝 장소의 유효성을 검사한다")
    void validateStartToEnd() {
        // Given
        final String startPlaceName = "루터회관";
        final Place start = new Place(startPlaceName, new Point(127.0, 37.0));
        final String endPlaceName = "선릉역";
        final Place end = new Place(endPlaceName, new Point(127.1, 37.1));
        final TravelMethod subway = TravelMethod.SUBWAY;
        final TravelMethod transfer = TravelMethod.TRANSFER;
        final int travelTime = 10;
        final String subwayLineName = "2호선";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Path(start, start, subway, travelTime, SubwayLine.fromTitle(subwayLineName)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("시작 장소(%s)와 끝 장소(%s)는 같을 수 없습니다.", startPlaceName, startPlaceName);

            softAssertions.assertThatThrownBy(() -> new Path(start, end, transfer, travelTime, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("환승 통로 이동에서의 시작 장소(%s)와 끝 장소(%s)는 다를 수 없습니다.", startPlaceName, endPlaceName);
        });
    }

    @Test
    @DisplayName("이동 수단에 따른 지하철 노선명의 유효성을 검사한다")
    void validateSubwayLine() {
        // Given
        final Place start = new Place("루터회관", new Point(127.0, 37.0));
        final Place end = new Place("선릉역", new Point(127.1, 37.1));
        final TravelMethod bus = TravelMethod.BUS;
        final TravelMethod subway = TravelMethod.SUBWAY;
        final int travelTime = 10;
        final String subwayLineName = "2호선";

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Path(start, end, bus, travelTime, SubwayLine.fromTitle(subwayLineName)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("지하철이 아니라면 호선 정보를 지닐 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Path(start, end, subway, travelTime, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("지하철인 경우 지하철 호선 정보가 필수입니다.");
        });
    }

}
