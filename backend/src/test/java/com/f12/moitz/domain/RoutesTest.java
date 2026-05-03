package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.subway.SubwayLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoutesTest {

    @Test
    @DisplayName("공평성 점수는 가중 점수로 비교한다")
    void calculateFairnessScore() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place startC = new Place("출발C", new Point(127.2, 37.2));
        final Place destination = new Place("도착", new Point(127.3, 37.3));

        final Routes betterRoutes = new Routes(java.util.List.of(
                route(startA, destination, 28, 1),
                route(startB, destination, 28, 1),
                route(startC, destination, 23, 1)
        ));
        final Routes worseRoutes = new Routes(java.util.List.of(
                route(startA, destination, 34, 2),
                route(startB, destination, 38, 2),
                route(startC, destination, 31, 1)
        ));

        assertThat(betterRoutes.calculateFairnessScore())
                .isLessThan(worseRoutes.calculateFairnessScore());
    }

    @Test
    @DisplayName("최대 이동시간이 늘어도 단일 수혜자 편향이 줄어든 후보를 우선할 수 있다")
    void calculateFairnessScore_ConsidersSkewPenalty() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place startC = new Place("출발C", new Point(127.2, 37.2));
        final Place destination = new Place("도착", new Point(127.3, 37.3));

        final Routes skewedRoutes = new Routes(java.util.List.of(
                route(startA, destination, 38, 0),
                route(startB, destination, 56, 0),
                route(startC, destination, 62, 0)
        ));
        final Routes lessSkewedRoutes = new Routes(java.util.List.of(
                route(startA, destination, 45, 0),
                route(startB, destination, 57, 0),
                route(startC, destination, 69, 0)
        ));

        assertThat(lessSkewedRoutes.calculateFairnessScore())
                .isLessThan(skewedRoutes.calculateFairnessScore());
    }

    @Test
    @DisplayName("공평성 하드 필터를 초과하면 허용되지 않는다")
    void isAcceptable() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place startC = new Place("출발C", new Point(127.2, 37.2));
        final Place destination = new Place("도착", new Point(127.3, 37.3));

        final Routes unacceptableRoutes = new Routes(java.util.List.of(
                route(startA, destination, 62, 1),
                route(startB, destination, 61, 2),
                route(startC, destination, 57, 1)
        ));

        assertThat(unacceptableRoutes.isAcceptable(DispersionPolicy.TIER_1)).isFalse();
    }

    @Test
    @DisplayName("평균 환승 횟수를 계산한다")
    void calculateAverageTransferCount() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place destination = new Place("도착", new Point(127.3, 37.3));

        final Routes routes = new Routes(java.util.List.of(
                route(startA, destination, 20, 0),
                route(startB, destination, 20, 1)
        ));

        assertThat(routes.calculateAverageTransferCount()).isEqualTo(0.5);
        assertThat(routes.calculateFairnessScore().getAverageTransferCount()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("환승 부담을 계산한다")
    void calculateTransferBurden() {
        final Place startA = new Place("출발A", new Point(127.0, 37.0));
        final Place startB = new Place("출발B", new Point(127.1, 37.1));
        final Place destination = new Place("도착", new Point(127.3, 37.3));

        final Routes routes = new Routes(java.util.List.of(
                route(startA, destination, 20, 0),
                route(startB, destination, 20, 2)
        ));

        assertThat(routes.calculateTransferBurden())
                .isEqualTo(new TransferBurden(1.0, 2, 2));
    }

    @Test
    @DisplayName("경로 묶음은 null이거나 비어있거나 null 경로를 포함할 수 없다")
    void constructor_ThrowsExceptionWhenRoutesAreInvalid() {
        final Route route = route(
                new Place("출발", new Point(127.0, 37.0)),
                new Place("도착", new Point(127.1, 37.1)),
                20,
                0
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new Routes(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로는 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Routes(List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로는 비어있거나 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new Routes(Arrays.asList(route, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이동 경로 목록에 null이 포함될 수 없습니다.");
        });
    }

    @Test
    @DisplayName("경로 묶음은 외부에서 변경할 수 없다")
    void getRoutes_ReturnsUnmodifiableList() {
        final List<Route> routeList = new ArrayList<>();
        routeList.add(route(
                new Place("출발", new Point(127.0, 37.0)),
                new Place("도착", new Point(127.1, 37.1)),
                20,
                0
        ));

        final Routes routes = new Routes(routeList);

        routeList.clear();

        assertThat(routes.getRoutes()).hasSize(1);
        assertThatThrownBy(() -> routes.getRoutes().add(route(
                new Place("다른 출발", new Point(127.2, 37.2)),
                new Place("다른 도착", new Point(127.3, 37.3)),
                20,
                0
        ))).isInstanceOf(UnsupportedOperationException.class);
    }

    private Route route(final Place start, final Place end, final int minutes, final int transferCount) {
        final java.util.List<Path> paths = new java.util.ArrayList<>();
        Place currentStart = start;
        for (int index = 0; index < transferCount; index++) {
            final Place transferStation = new Place(
                    "환승" + index,
                    new Point(127.4 + index, 37.4 + index)
            );
            paths.add(new Path(currentStart, transferStation, TravelMethod.SUBWAY, minutes * 60, SubwayLine.fromTitle("2호선")));
            paths.add(new Path(transferStation, transferStation, TravelMethod.TRANSFER, 0, null));
            currentStart = transferStation;
        }
        paths.add(new Path(currentStart, end, TravelMethod.SUBWAY, minutes * 60, SubwayLine.fromTitle("3호선")));
        return new Route(paths);
    }
}
