package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Routes {

    private static final double FAIRNESS_FACTOR = 2.0;

    private final List<Route> routes;

    public Routes(final List<Route> routes) {
        validate(routes);
        this.routes = routes;
    }

    private void validate(final List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("이동 경로는 비어있거나 null일 수 없습니다.");
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

        return max - min <= calculateAverageTravelTime() * FAIRNESS_FACTOR;
    }

    public int calculateAverageTravelTime() {
        return (int) routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .average()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

}
