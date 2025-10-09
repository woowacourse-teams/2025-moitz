package com.f12.moitz.domain.subway;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import lombok.Getter;

@Getter
public class SubwayStation extends Place {

    public SubwayStation(final String name, final Point point) {
        super(name, point);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

}
