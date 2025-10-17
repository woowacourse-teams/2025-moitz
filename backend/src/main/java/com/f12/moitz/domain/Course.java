package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Course {

    private final List<? extends Place> places;

    public Course(final List<? extends Place> places) {
        validate(places);
        this.places = places;
    }

    private void validate(final List<? extends Place> places) {
        if (places == null || places.isEmpty()) {
            throw new IllegalArgumentException("이동 코스는 필수입니다.");
        }
    }

    public int size() {
        return places.size();
    }

}
