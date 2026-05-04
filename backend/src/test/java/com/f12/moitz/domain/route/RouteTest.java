package com.f12.moitz.domain.route;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.SubwayLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteTest {

    @Test
    @DisplayName("예외가 발생하지 않고 경로가 생성된다")
    void doesNotThrow() {
        // Given
        final Place startPlace = new Place("루터회관", new Point(127.0, 37.0));
        final Place endPlace = new Place("선릉역", new Point(127.1, 37.1));
        final String subwayLineName = "2호선";

        final Path path = new Path(startPlace, endPlace, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle(subwayLineName));
        final List<Path> paths = List.of(path);

        // When & Then
        assertThatNoException().isThrownBy(() -> new Route(paths));
    }

    @Test
    @DisplayName("경로 목록이 null이거나 비어있다면 경로를 생성할 수 없다")
    void isThrownByInvalidPaths() {
        // Given
        final List<Path> pathsNull = null;
        final List<Path> pathsEmpty = Collections.emptyList();

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Route(pathsNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로는 반드시 존재해야 합니다.");

            softAssertions.assertThatThrownBy(() -> new Route(pathsEmpty))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로는 반드시 존재해야 합니다.");

            softAssertions.assertThatThrownBy(() -> new Route(Arrays.asList(createSubwayPath("잠실역", "선릉역"), null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로에 null이 포함될 수 없습니다.");
        });
    }

    @Test
    @DisplayName("총 소요 시간을 올바르게 계산한다")
    void calculateTotalTravelTime() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 600, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 1200, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);

        // When
        final int totalTravelTime = route.calculateTotalTravelTime();

        // Then
        assertThat(totalTravelTime).isEqualTo(30);
    }

    @Test
    @DisplayName("환승 횟수를 올바르게 계산한다")
    void calculateTransferCount() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);

        // When
        final int transferCount = route.calculateTransferCount();

        // Then
        assertThat(transferCount).isZero();
    }

    @Test
    @DisplayName("환승 경로 개수로 환승 횟수를 계산한다")
    void calculateTransferCount_ByTransferPath() {
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place transferPlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));

        final Route route = new Route(List.of(
                new Path(startPlace, transferPlace, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle("2호선")),
                Path.transfer(transferPlace, 0),
                new Path(transferPlace, endPlace, TravelMethod.SUBWAY, 20, SubwayLine.fromTitle("신분당선"))
        ));

        assertThat(route.calculateTransferCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("경로 목록은 외부에서 변경할 수 없다")
    void getPaths_ReturnsUnmodifiableList() {
        final List<Path> paths = new ArrayList<>();
        paths.add(createSubwayPath("잠실역", "선릉역"));

        final Route route = new Route(paths);

        paths.clear();

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(route.getPaths()).hasSize(1);
            softAssertions.assertThatThrownBy(() -> route.getPaths().add(createSubwayPath("선릉역", "강남역")))
                    .isInstanceOf(UnsupportedOperationException.class);
        });
    }

    @Test
    @DisplayName("출발지와 도착지를 올바르게 반환한다")
    void getStartAndEndPlace() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, SubwayLine.fromTitle(subwayLineName));
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, SubwayLine.fromTitle(subwayLineName));

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);

        // When
        final Place actualStartPlace = route.getStartPlace();
        final Place actualEndPlace = route.getEndPlace();

        // Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actualStartPlace).isEqualTo(startPlace);
            softAssertions.assertThat(actualEndPlace).isEqualTo(endPlace);
        });
    }

    private Path createSubwayPath(final String startName, final String endName) {
        return new Path(
                new Place(startName, new Point(127.0, 37.0)),
                new Place(endName, new Point(127.1, 37.1)),
                TravelMethod.SUBWAY,
                10,
                SubwayLine.fromTitle("2호선")
        );
    }

}
