package com.f12.moitz.domain;

import lombok.Getter;

@Getter
public class Point {

    private final double x;
    private final double y;

    public Point(double x, double y) {
        validate(x, y);
        this.x = x;
        this.y = y;
    }

    private void validate(final double x, final double y) {
        if (x < 124 || x > 132) {
            throw new IllegalArgumentException("X(경도)는 대한민국 영역 내에 있어야 합니다. 범위: 124 ~ 132");
        }
        if (y < 33 || y > 43) {
            throw new IllegalArgumentException("Y(위도)는 대한민국 영역 내에 있어야 합니다. 범위: 33 ~ 43");
        }
    }

}
