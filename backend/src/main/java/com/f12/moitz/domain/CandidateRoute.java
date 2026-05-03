package com.f12.moitz.domain;

import lombok.Getter;

@Getter
public class CandidateRoute {

    private final Route route;
    private final Course course;

    public CandidateRoute(final Route route, final Course course) {
        validate(route, course);
        this.route = route;
        this.course = course;
    }

    private void validate(final Route route, final Course course) {
        if (route == null) {
            throw new IllegalArgumentException("후보 경로는 필수입니다.");
        }
        if (course == null) {
            throw new IllegalArgumentException("후보 이동 코스는 필수입니다.");
        }
    }

}
