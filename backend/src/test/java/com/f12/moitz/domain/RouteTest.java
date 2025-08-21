package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

        final Path path = new Path(startPlace, endPlace, TravelMethod.SUBWAY, 10, subwayLineName);
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

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 600, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 1200, subwayLineName);

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

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, subwayLineName);

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);

        // When
        final int transferCount = route.calculateTransferCount();

        // Then
        assertThat(transferCount).isZero();
    }

    @Test
    @DisplayName("출발지와 도착지를 올바르게 반환한다")
    void getStartAndEndPlace() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, subwayLineName);

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
}
