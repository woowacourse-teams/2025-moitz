package com.f12.moitz.domain;

import java.util.Objects;
import lombok.Getter;

@Getter
public class Place {

    private final String name;
    private final Point point;

    public Place(final String name, final Point point) {
        validate(name, point);
        this.name = name;
        this.point = point;
    }

    private void validate(final String name, final Point point) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("장소 이름은 필수입니다.");
        }
        if (point == null) {
            throw new IllegalArgumentException("좌표는 필수입니다.");
        }
    }

    /*
    장소의 좌표만 같아도 같은 장소로 간주해야 하나?
    만약 이름이 천호역(8호선)과 천호역 8호선이 있다면 다른 장소로 취급할텐데?
    다만 좌표는 같지만 (같은 건물) 다른 층의 경우 다른 장소로 봐야하지 않나?
    -> 현재 Place를 공유하고 있기 때문에 발생하는 문제인듯
    */

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Place place)) {
            return false;
        }
        return Objects.equals(name, place.name) && Objects.equals(point, place.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, point);
    }
}
