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

    public boolean isAcceptable(final DispersionPolicy dispersionPolicy) {
        return dispersionPolicy.isAcceptable(calculateFairnessScore());
    }

    public FairnessScore calculateFairnessScore() {
        final int minTravelTime = calculateMinTravelTime();
        final double medianTravelTime = calculateMedianTravelTime();
        final int maxTravelTime = calculateMaxTravelTime();
        return new FairnessScore(
                maxTravelTime,
                calculateMaxTransferCount(),
                calculateAverageTransferCount(),
                calculateTransferDiff(),
                maxTravelTime - minTravelTime,
                calculateAverageTravelTime(),
                medianTravelTime - minTravelTime,
                maxTravelTime - medianTravelTime
        );
    }

    public TransferBurden calculateTransferBurden() {
        return new TransferBurden(
                calculateAverageTransferCount(),
                calculateMaxTransferCount(),
                calculateTransferDiff()
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

    public double calculateMedianTravelTime() {
        final List<Integer> sortedTravelTimes = routes.stream()
                .map(Route::calculateTotalTravelTime)
                .sorted()
                .toList();
        final int size = sortedTravelTimes.size();
        if (size == 0) {
            throw new IllegalStateException("경로 목록이 비어 있습니다.");
        }
        if (size % 2 == 1) {
            return sortedTravelTimes.get(size / 2);
        }
        return (sortedTravelTimes.get(size / 2 - 1) + sortedTravelTimes.get(size / 2)) / 2.0;
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

    public double calculateAverageTransferCount() {
        return routes.stream()
                .mapToInt(Route::calculateTransferCount)
                .average()
                .orElseThrow(() -> new IllegalStateException("경로 목록이 비어 있습니다."));
    }

    public int calculateTransferDiff() {
        return calculateMaxTransferCount() - calculateMinTransferCount();
    }

}
