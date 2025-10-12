package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Route {

    private final List<Path> paths;

    public Route(final List<Path> paths) {
        validate(paths);
        this.paths = paths;
    }

    private void validate(final List<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            throw new IllegalArgumentException("이동 경로는 반드시 존재해야 합니다.");
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
