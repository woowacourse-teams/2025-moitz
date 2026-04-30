package com.f12.moitz.domain;

import java.util.Comparator;

public class RouteCandidateComparators {

    private static final int SHORT_AVERAGE_TRAVEL_TIME_MINUTES = 10;
    private static final int MEDIUM_AVERAGE_TRAVEL_TIME_MINUTES = 30;
    private static final double SHORT_TRAVEL_TIME_TOLERANCE_RATIO = 0.5;
    private static final double MEDIUM_TRAVEL_TIME_TOLERANCE_RATIO = 0.3;
    private static final double LONG_TRAVEL_TIME_TOLERANCE_RATIO = 0.2;

    public Comparator<RouteCandidate> getByTag(final CandidateSelectionTag tag) {
        return switch (tag) {
            case EFFICIENCY -> byEfficiency();
            case FAIRNESS -> byFairness();
            case MAX_BURDEN_RELIEF -> byMaxBurdenRelief();
            case TRANSFER -> byTransfer();
            case GENERAL -> byGeneral();
        };
    }

    private Comparator<RouteCandidate> byEfficiency() {
        return Comparator.comparingInt((RouteCandidate candidate) -> candidate.calculateFairnessScore()
                        .getAverageTravelTime())
                .thenComparingInt(candidate -> candidate.calculateFairnessScore().getMaxTravelTime())
                .thenComparing(RouteCandidate::calculateFairnessScore);
    }

    private Comparator<RouteCandidate> byFairness() {
        return (left, right) -> {
            final FairnessScore leftScore = left.calculateFairnessScore();
            final FairnessScore rightScore = right.calculateFairnessScore();
            final boolean leftTolerable = isTolerablyFair(leftScore);
            final boolean rightTolerable = isTolerablyFair(rightScore);

            if (leftTolerable != rightTolerable) {
                return Boolean.compare(rightTolerable, leftTolerable);
            }
            if (leftTolerable) {
                return compareTolerablyFairScores(leftScore, rightScore);
            }
            return compareStrictFairnessScores(leftScore, rightScore);
        };
    }

    private boolean isTolerablyFair(final FairnessScore score) {
        return score.getTimeDiff() <= resolveFairnessToleranceMinutes(score);
    }

    private int resolveFairnessToleranceMinutes(final FairnessScore score) {
        return (int) Math.ceil(score.getAverageTravelTime() * resolveFairnessToleranceRatio(score));
    }

    private double resolveFairnessToleranceRatio(final FairnessScore score) {
        if (score.getAverageTravelTime() <= SHORT_AVERAGE_TRAVEL_TIME_MINUTES) {
            return SHORT_TRAVEL_TIME_TOLERANCE_RATIO;
        }
        if (score.getAverageTravelTime() <= MEDIUM_AVERAGE_TRAVEL_TIME_MINUTES) {
            return MEDIUM_TRAVEL_TIME_TOLERANCE_RATIO;
        }
        return LONG_TRAVEL_TIME_TOLERANCE_RATIO;
    }

    private int compareTolerablyFairScores(final FairnessScore left, final FairnessScore right) {
        return Comparator.comparingInt(FairnessScore::getAverageTravelTime)
                .thenComparingInt(FairnessScore::getMaxTravelTime)
                .thenComparingInt(FairnessScore::getTimeDiff)
                .thenComparingDouble(FairnessScore::getSkewRange)
                .thenComparingDouble(FairnessScore::getSkewImbalance)
                .thenComparing(FairnessScore::compareTo)
                .compare(left, right);
    }

    private int compareStrictFairnessScores(final FairnessScore left, final FairnessScore right) {
        return Comparator.comparingDouble(FairnessScore::getSkewRange)
                .thenComparingInt(FairnessScore::getTimeDiff)
                .thenComparingDouble(FairnessScore::getSkewImbalance)
                .thenComparingInt(FairnessScore::getMaxTravelTime)
                .thenComparing(FairnessScore::compareTo)
                .compare(left, right);
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

    private Comparator<RouteCandidate> byGeneral() {
        return Comparator.comparing(RouteCandidate::calculateFairnessScore);
    }

}
