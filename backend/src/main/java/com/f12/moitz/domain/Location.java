package com.f12.moitz.domain;

import java.util.List;
import java.util.OptionalInt;
import lombok.Getter;

@Getter
public class Location {

    private final Place recommendedPlace;
    private final List<Route> routes;

    public Location(final Place recommendedPlace, final List<Route> routes) {
        validate(recommendedPlace, routes);
        this.recommendedPlace = recommendedPlace;
        this.routes = routes;
    }

    private void validate(final Place recommendedPlace, final List<Route> routes) {
        if (recommendedPlace == null) {
            throw new IllegalArgumentException("추천 장소는 필수입니다.");
        }
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("경로 목록은 필수입니다.");
        }
    }

    public boolean isAcceptable() {
        final int max = routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .max()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));

        final int min = routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .min()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));

        return max - min > calculateAverageTravelTime() * 1.5;
    }

    private int calculateAverageTravelTime() {
        return (int) routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .average()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

}
