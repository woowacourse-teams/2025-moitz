package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoutesTest {

    @Test
    @DisplayName("예외가 발생하지 않고 경로 목록이 생성된다")
    void doesNotThrow() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, subwayLineName);

        final List<Path> paths = List.of(path1, path2);
        final Route route = new Route(paths);
        final List<Route> routes = List.of(route);

        // When & Then
        assertThatNoException().isThrownBy(() -> new Routes(routes));
    }

    @Test
    @DisplayName("경로 목록이 null이거나 비어있다면 경로 목록을 생성할 수 없다")
    void isThrownByInvalidRoutes() {
        // Given
        final List<Route> routesNull = null;
        final List<Route> routesEmpty = Collections.emptyList();

        // When & Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Routes(routesNull))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로는 비어있거나 null일 수 없습니다.");

            softAssertions.assertThatThrownBy(() -> new Routes(routesEmpty))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로는 비어있거나 null일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("경로들의 공정성을 올바르게 판단한다")
    void isAcceptable() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 50, subwayLineName);

        final List<Path> paths1 = List.of(path2);
        final List<Path> paths2 = List.of(path1);

        final Route route1 = new Route(paths1);
        final Route route2 = new Route(paths2);

        final Routes routes1 = new Routes(List.of(route1, route1));
        final Routes routes2 = new Routes(List.of(route1, route2));

        // When
        final boolean acceptable1 = routes1.isAcceptable();
        final boolean acceptable2 = routes2.isAcceptable();

        // Then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(acceptable1).isTrue();
            softAssertions.assertThat(acceptable2).isFalse();
        });
    }

    @Test
    @DisplayName("평균 소요 시간을 올바르게 계산한다")
    void calculateAverageTravelTime() {
        // Given
        final Place startPlace = new Place("잠실역", new Point(127.0, 37.0));
        final Place intermediatePlace = new Place("선릉역", new Point(127.1, 37.1));
        final Place endPlace = new Place("강남역", new Point(127.2, 37.2));
        final String subwayLineName = "2호선";

        final Path path1 = new Path(startPlace, intermediatePlace, TravelMethod.SUBWAY, 10, subwayLineName);
        final Path path2 = new Path(intermediatePlace, endPlace, TravelMethod.SUBWAY, 20, subwayLineName);

        final List<Path> paths1 = List.of(path2);
        final List<Path> paths2 = List.of(path1);

        final Route route1 = new Route(paths1);
        final Route route2 = new Route(paths2);

        final Routes routes = new Routes(List.of(route1, route2));

        // When
        final int averageTravelTime = routes.calculateAverageTravelTime();

        // Then
        assertThat(averageTravelTime).isEqualTo(15);
    }
}
