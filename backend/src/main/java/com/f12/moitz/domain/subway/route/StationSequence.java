package com.f12.moitz.domain.subway.route;

import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.route.CandidateRoute;
import com.f12.moitz.domain.route.Course;
import com.f12.moitz.domain.route.Path;
import com.f12.moitz.domain.route.Route;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.ArrayList;
import java.util.List;

public class StationSequence {

    private final List<StationSegment> segments;

    public StationSequence(final List<StationSegment> segments) {
        validate(segments);
        this.segments = new ArrayList<>(segments);
    }

    private void validate(final List<StationSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("지하철역 구간 목록은 반드시 존재해야 합니다.");
        }
    }

    private List<Path> groupByLine() {
        final List<Path> paths = new ArrayList<>();

        SubwayLine currentLine = segments.getFirst().getLine();
        SubwayStation startStation = segments.getFirst().getStation();
        int totalTime = 0;

        for (int i = 1; i < segments.size(); i++) {
            final StationSegment current = segments.get(i);

            // 이전 구간의 실제 이동 시간 계산 (Edge에서 직접 가져오기)
            final StationSegment previous = segments.get(i - 1);
            final int segmentTime = previous.getTimeInSeconds();
            totalTime += segmentTime;

            // 환승 경로이거나 마지막 역인 경우
            if (current.isTransfer() || segments.getLast().equals(current)) {
                final SubwayStation endStation = current.getStation();

                final Path path = Path.subway(
                        startStation,
                        endStation,
                        totalTime,
                        currentLine
                );
                paths.add(path);

                // 환승 처리: 같은 역에서 다른 호선으로 환승 (마지막 역이 아닌 경우에만)
                if (current.isTransfer() && i < segments.size() - 1) {
                    // 환승 시간 계산: 환승 Edge의 시간 직접 활용
                    final int transferTime = current.getTimeInSeconds();

                    final Path transferPath = Path.transfer(
                            endStation,
                            transferTime
                    );
                    paths.add(transferPath);

                    // 다음 구간 초기화
                    i++;
                    final StationSegment next = segments.get(i);
                    currentLine = next.getLine();
                    startStation = current.getStation();
                    totalTime = 0;
                }
            }
        }

        return paths;
    }

    public Route toRoute() {
        return new Route(groupByLine());
    }

    private List<Point> getPoints() {
        return segments.stream()
                .map(StationSegment::getStation)
                .map(SubwayStation::getPoint)
                .distinct()
                .toList();
    }

    public Course toCourse() {
        return new Course(getPoints());
    }

    public CandidateRoute toCandidateRoute() {
        return new CandidateRoute(toRoute(), toCourse());
    }

}
