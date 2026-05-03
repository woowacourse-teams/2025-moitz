package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Course {

    private final List<Point> points;

    public Course(final List<Point> points) {
        validate(points);
        this.points = List.copyOf(points);
    }

    private void validate(final List<Point> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("이동 코스의 좌표 목록은 필수입니다.");
        }
        if (points.stream().anyMatch(point -> point == null)) {
            throw new IllegalArgumentException("이동 코스의 좌표 목록에 null이 포함될 수 없습니다.");
        }
    }

    public int size() {
        return points.size();
    }

}
