package com.f12.moitz.domain;

import lombok.Getter;

@Getter
public class RouteCandidate {

    private final Place place;
    private final Routes routes;

    public RouteCandidate(final Place place, final Routes routes) {
        if (place == null) {
            throw new IllegalArgumentException("후보 장소는 필수입니다.");
        }
        if (routes == null) {
            throw new IllegalArgumentException("후보 경로는 필수입니다.");
        }
        this.place = place;
        this.routes = routes;
    }

    public FairnessScore calculateFairnessScore() {
        return routes.calculateFairnessScore();
    }

    public boolean isAcceptable(final DispersionPolicy dispersionPolicy) {
        return routes.isAcceptable(dispersionPolicy);
    }

}
