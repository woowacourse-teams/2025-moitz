package com.f12.moitz.domain.place;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Place {

    private String name;
    private Point point;

    public Place(final String name, final Point point) {
        validate(name, point);
        this.name = name;
        this.point = point;
    }

    private void validate(final String name, final Point point) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("이름은 비어있거나 null일 수 없습니다.");
        }
        if (point == null) {
            throw new IllegalArgumentException("좌표는 필수입니다.");
        }
    }

    public boolean isNameMatch(final String name) {
        return this.name.equals(name);
    }

}
