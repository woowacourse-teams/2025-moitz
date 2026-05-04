package com.f12.moitz.domain.route;

import com.f12.moitz.domain.place.Place;

public class OriginDestination {

    private final Place origin;
    private final Place destination;

    public OriginDestination(final Place origin, final Place destination) {
        validate(origin, destination);
        this.origin = origin;
        this.destination = destination;
    }

    private void validate(final Place origin, final Place destination) {
        if (origin == null) {
            throw new IllegalArgumentException("출발지는 필수입니다.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("도착지는 필수입니다.");
        }
    }

    public Place getOrigin() {
        return origin;
    }

    public Place getDestination() {
        return destination;
    }

}
