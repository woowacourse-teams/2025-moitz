package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Routes {

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
        return calculateFairnessScore().isAcceptable();
    }

    public FairnessScore calculateFairnessScore() {
        return new FairnessScore(
                calculateMaxTravelTime(),
                calculateMaxTransferCount(),
                calculateTransferDiff(),
                calculateTimeDiff(),
                calculateAverageTravelTime()
        );
    }

    public int calculateMaxTravelTime() {
        return routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .max()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

    public int calculateMinTravelTime() {
        return routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .min()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

    public int calculateAverageTravelTime() {
        return (int) routes.stream()
                .mapToInt(Route::calculateTotalTravelTime)
                .average()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

    public int calculateTimeDiff() {
        return calculateMaxTravelTime() - calculateMinTravelTime();
    }

    public int calculateMaxTransferCount() {
        return routes.stream()
                .mapToInt(Route::calculateTransferCount)
                .max()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

    public int calculateMinTransferCount() {
        return routes.stream()
                .mapToInt(Route::calculateTransferCount)
                .min()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

    public int calculateTransferDiff() {
        return calculateMaxTransferCount() - calculateMinTransferCount();
    }

}
