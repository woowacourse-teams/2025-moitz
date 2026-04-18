package com.f12.moitz.domain;

import java.util.Comparator;

public class RouteCandidateComparators {

    public Comparator<RouteCandidate> getByBucket(final CandidateSelectionBucket bucket) {
        return switch (bucket) {
            case EFFICIENCY -> byEfficiency();
            case FAIRNESS -> byFairness();
            case MAX_BURDEN_RELIEF -> byMaxBurdenRelief();
            case TRANSFER -> byTransfer();
        };
    }

    private Comparator<RouteCandidate> byEfficiency() {
                return Comparator.comparingInt((RouteCandidate candidate) -> candidate.calculateFairnessScore()
                        .getAverageTravelTime())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getMaxTravelTime())
                .thenComparing(RouteCandidate::calculateFairnessScore);
    }

    private Comparator<RouteCandidate> byFairness() {
        return Comparator.comparingDouble((RouteCandidate candidate) -> candidate.calculateFairnessScore().getSkewRange())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getTimeDiff())
                .thenComparingDouble(candidate -> candidate.calculateFairnessScore().getSkewImbalance())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getMaxTravelTime())
                .thenComparing(RouteCandidate::calculateFairnessScore);
    }

    private Comparator<RouteCandidate> byMaxBurdenRelief() {
        return Comparator.comparingInt((RouteCandidate candidate) -> candidate.calculateFairnessScore().getMaxTravelTime())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getTimeDiff())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getAverageTravelTime())
                .thenComparing(RouteCandidate::calculateFairnessScore);
    }

    private Comparator<RouteCandidate> byTransfer() {
        return Comparator.comparingInt((RouteCandidate candidate) -> candidate.calculateFairnessScore().getMaxTransferCount())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getTransferDiff())
                .thenComparing(RouteCandidate::calculateFairnessScore)
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getMaxTravelTime());
    }

}
