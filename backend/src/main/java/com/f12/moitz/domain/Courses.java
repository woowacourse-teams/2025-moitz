package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Courses {

    private final List<Course> courses;

    public Courses(final List<Course> courses) {
        validate(courses);
        this.courses = courses;
    }

    private void validate(final List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            throw new IllegalArgumentException("이동 코스는 비어있거나 null일 수 없습니다.");
        }
    }

}
