package com.f12.moitz.domain.route;

import java.util.List;
import lombok.Getter;

@Getter
public class Courses {

    private final List<Course> courses;

    public Courses(final List<Course> courses) {
        validate(courses);
        this.courses = List.copyOf(courses);
    }

    private void validate(final List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            throw new IllegalArgumentException("이동 코스는 비어있거나 null일 수 없습니다.");
        }
        if (courses.stream().anyMatch(course -> course == null)) {
            throw new IllegalArgumentException("이동 코스 목록에 null이 포함될 수 없습니다.");
        }
    }

    public int size() {
        return courses.size();
    }

    public Course get(final int index) {
        return courses.get(index);
    }

}
