package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.subway.SubwayLine;
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

        assertThat(unacceptableRoutes.isAcceptable()).isFalse();
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
