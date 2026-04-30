package com.f12.moitz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.domain.subway.SubwayLine;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteCandidateComparatorsTest {

    private final RouteCandidateComparators comparators = new RouteCandidateComparators();

    @Test
    @DisplayName("공평 후보는 이동 시간 차이가 허용 범위 안이면 평균 이동 시간이 짧은 후보를 우선한다")
    void fairnessComparator_PrioritizesShortAverageWithinTolerance() {
        final RouteCandidate exactButLong = routeCandidate("완전공평장거리역", 28, 28);
        final RouteCandidate shortAndTolerablyFair = routeCandidate("실용공평단거리역", 2, 4);

        final List<RouteCandidate> sortedCandidates = List.of(exactButLong, shortAndTolerablyFair).stream()
                .sorted(comparators.getByTag(CandidateSelectionTag.FAIRNESS))
                .toList();

        assertThat(sortedCandidates)
                .extracting(candidate -> candidate.getPlace().getName())
                .containsExactly("실용공평단거리역", "완전공평장거리역");
    }

    @Test
    @DisplayName("공평 후보는 이동 시간 차이가 허용 범위를 벗어나면 기존 공평성 기준으로 비교한다")
    void fairnessComparator_UsesStrictFairnessOutsideTolerance() {
        final RouteCandidate unfairButShort = routeCandidate("불공평단거리역", 1, 20);
        final RouteCandidate exactButLong = routeCandidate("완전공평장거리역", 28, 28);

        final List<RouteCandidate> sortedCandidates = List.of(unfairButShort, exactButLong).stream()
                .sorted(comparators.getByTag(CandidateSelectionTag.FAIRNESS))
                .toList();

        assertThat(sortedCandidates)
                .extracting(candidate -> candidate.getPlace().getName())
                .containsExactly("완전공평장거리역", "불공평단거리역");
    }

    @Test
    @DisplayName("최소 환승 후보는 평균 환승 횟수가 적은 후보를 우선한다")
    void transferComparator_PrioritizesAverageTransferCount() {
        final RouteCandidate lowAverage = routeCandidateWithTransfers("낮은평균환승역", List.of(0, 1));
        final RouteCandidate lowMax = routeCandidateWithTransfers("낮은최대환승역", List.of(1, 1));
        final RouteCandidate highMax = routeCandidateWithTransfers("높은최대환승역", List.of(0, 2));

        final List<RouteCandidate> sortedCandidates = List.of(highMax, lowMax, lowAverage).stream()
                .sorted(comparators.getByTag(CandidateSelectionTag.TRANSFER))
                .toList();

        assertThat(sortedCandidates)
                .extracting(candidate -> candidate.getPlace().getName())
                .containsExactly("낮은평균환승역", "낮은최대환승역", "높은최대환승역");
    }

    private RouteCandidate routeCandidate(final String name, final int firstTravelTimeMinutes, final int secondTravelTimeMinutes) {
        final Place start1 = new Place("출발1역", new Point(127.0, 37.0));
        final Place start2 = new Place("출발2역", new Point(127.1, 37.1));
        final Place end = new Place(name, new Point(127.2, 37.2));

        return new RouteCandidate(
                end,
                new Routes(List.of(
                        route(start1, end, firstTravelTimeMinutes),
                        route(start2, end, secondTravelTimeMinutes)
                ))
        );
    }

    private Route route(final Place start, final Place end, final int travelTimeMinutes) {
        return new Route(List.of(new Path(
                start,
                end,
                TravelMethod.SUBWAY,
                travelTimeMinutes * 60,
                SubwayLine.fromTitle("2호선")
        )));
    }

    private RouteCandidate routeCandidateWithTransfers(final String name, final List<Integer> transferCounts) {
        final Place end = new Place(name, new Point(127.2, 37.2));
        final List<Route> routes = java.util.stream.IntStream.range(0, transferCounts.size())
                .mapToObj(index -> routeWithTransfers(
                        new Place("출발" + index + "역", new Point(127.0 + index, 37.0 + index)),
                        end,
                        transferCounts.get(index)
                ))
                .toList();
        return new RouteCandidate(end, new Routes(routes));
    }

    private Route routeWithTransfers(final Place start, final Place end, final int transferCount) {
        final List<Path> paths = new java.util.ArrayList<>();
        Place currentStart = start;
        for (int index = 0; index < transferCount; index++) {
            final Place transferStation = new Place(
                    start.getName() + "환승" + index,
                    new Point(127.4 + index, 37.4 + index)
            );
            paths.add(new Path(currentStart, transferStation, TravelMethod.SUBWAY, 0, SubwayLine.fromTitle("2호선")));
            paths.add(new Path(transferStation, transferStation, TravelMethod.TRANSFER, 0, null));
            currentStart = transferStation;
        }
        paths.add(new Path(currentStart, end, TravelMethod.SUBWAY, 20 * 60, SubwayLine.fromTitle("3호선")));
        return new Route(paths);
    }
}
