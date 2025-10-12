package com.f12.moitz.domain;

import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import lombok.Getter;

@Getter
public class Route {

    private final List<Path> paths;
    private final List<SubwayStation> stations;

    public Route(final List<Path> paths, final List<SubwayStation> stations) {
        validate(paths, stations);
        this.paths = paths;
        this.stations = stations;
    }

    private void validate(final List<Path> paths, final List<SubwayStation> stations) {
        if (paths == null || paths.isEmpty()) {
            throw new IllegalArgumentException("이동 경로는 반드시 존재해야 합니다.");
        }
        if (stations == null || stations.isEmpty()) {
            throw new IllegalArgumentException("전체 경유 지하철역은 반드시 존재해야 합니다.");
        }
    }

    public int calculateTotalTravelTime() {
        return paths.stream()
                .mapToInt(path -> (int) path.getTravelTime().toMinutes())
                .sum();
    }

    public int calculateTransferCount() {
        return paths.size() > 2 ? paths.size() / 2 : 0;
    }

    public Place getStartPlace() {
        return paths.getFirst().getStart();
    }

    public Place getEndPlace() {
        return paths.getLast().getEnd();
    }

}
