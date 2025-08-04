package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Location {

    private final Place targetPlace;
    private final List<Route> routes;
    private final List<RecommendedPlace> recommendedPlaces;

    public Location(final Place targetPlace, final List<Route> routes, final List<RecommendedPlace> recommendedPlaces) {
        validate(targetPlace, routes, recommendedPlaces);
        this.targetPlace = targetPlace;
        this.routes = routes;
        this.recommendedPlaces = recommendedPlaces;
    }

    private void validate(final Place targetPlace, final List<Route> routes, final List<RecommendedPlace> recommendedPlaces) {
        if (targetPlace == null) {
            throw new IllegalArgumentException("목표 장소는 필수입니다.");
        }
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("경로 목록은 비어 있을 수 없습니다.");
        }
        if (recommendedPlaces == null || recommendedPlaces.isEmpty()) {
            throw new IllegalArgumentException("추천 장소 목록은 비어 있을 수 없습니다.");
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

    public int calculateAverageTravelTime() {
        return (int) routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .average()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

}
