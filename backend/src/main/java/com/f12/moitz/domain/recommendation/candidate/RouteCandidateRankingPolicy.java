package com.f12.moitz.domain.recommendation.candidate;

import java.util.Comparator;
import java.util.function.Function;

public class RouteCandidateRankingPolicy {

    private final FairnessTolerancePolicy fairnessTolerancePolicy = new FairnessTolerancePolicy();

    public Comparator<RouteCandidate> comparatorFor(
            final CandidateSelectionTag tag,
            final Function<RouteCandidate, FairnessScore> scoreResolver
    ) {
        return switch (tag) {
            case EFFICIENCY -> prioritizeEfficiency(scoreResolver);
            case FAIRNESS -> prioritizeFairness(scoreResolver);
            case MAX_BURDEN_RELIEF -> prioritizeMaxBurdenRelief(scoreResolver);
            case TRANSFER -> prioritizeTransfer(scoreResolver);
            case GENERAL -> prioritizeGeneral(scoreResolver);
        };
    }

    private Comparator<RouteCandidate> prioritizeEfficiency(final Function<RouteCandidate, FairnessScore> scoreResolver) {
        return Comparator.comparingInt((RouteCandidate candidate) -> scoreResolver.apply(candidate).getAverageTravelTime())
                .thenComparingInt(candidate -> scoreResolver.apply(candidate).getMaxTravelTime())
                .thenComparing(scoreResolver);
    }

    private Comparator<RouteCandidate> prioritizeFairness(final Function<RouteCandidate, FairnessScore> scoreResolver) {
        return (left, right) -> {
            final FairnessScore leftScore = scoreResolver.apply(left);
            final FairnessScore rightScore = scoreResolver.apply(right);
            final boolean leftTolerable = fairnessTolerancePolicy.isTolerable(leftScore);
            final boolean rightTolerable = fairnessTolerancePolicy.isTolerable(rightScore);

            if (leftTolerable != rightTolerable) {
                return Boolean.compare(rightTolerable, leftTolerable);
            }
            if (leftTolerable) {
                return compareTolerablyFairScores(leftScore, rightScore);
            }
            return compareStrictFairnessScores(leftScore, rightScore);
        };
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

    private Comparator<RouteCandidate> prioritizeMaxBurdenRelief(
            final Function<RouteCandidate, FairnessScore> scoreResolver
    ) {
        return Comparator.comparingInt((RouteCandidate candidate) -> scoreResolver.apply(candidate).getMaxTravelTime())
                .thenComparingInt(candidate -> scoreResolver.apply(candidate).getTimeDiff())
                .thenComparingInt(candidate -> scoreResolver.apply(candidate).getAverageTravelTime())
                .thenComparing(scoreResolver);
    }

    private Comparator<RouteCandidate> prioritizeTransfer(final Function<RouteCandidate, FairnessScore> scoreResolver) {
        return Comparator.comparing((RouteCandidate candidate) -> scoreResolver.apply(candidate).getTransferBurden())
                .thenComparingInt(candidate -> scoreResolver.apply(candidate).getAverageTravelTime())
                .thenComparingInt(candidate -> scoreResolver.apply(candidate).getMaxTravelTime())
                .thenComparing(scoreResolver);
    }

    private Comparator<RouteCandidate> prioritizeGeneral(final Function<RouteCandidate, FairnessScore> scoreResolver) {
        return Comparator.comparing(scoreResolver);
    }

}
