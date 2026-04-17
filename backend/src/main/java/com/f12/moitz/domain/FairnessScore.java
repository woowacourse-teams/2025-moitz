package com.f12.moitz.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FairnessScore implements Comparable<FairnessScore> {

    private static final double SKEW_RANGE_WEIGHT = 0.8;
    private static final double SKEW_IMBALANCE_WEIGHT = 0.5;
    private static final double TRANSFER_WEIGHT = 3.0;
    private static final double AVERAGE_TRAVEL_TIME_WEIGHT = 0.1;

    private final int maxTravelTime;
    private final int maxTransferCount;
    private final int transferDiff;
    private final int timeDiff;
    private final int averageTravelTime;
    private final double lowerSkew;
    private final double upperSkew;
    private final double skewRange;
    private final double skewImbalance;
    private final double weightedScore;

    public FairnessScore(
            final int maxTravelTime,
            final int maxTransferCount,
            final int transferDiff,
            final int timeDiff,
            final int averageTravelTime
    ) {
        this(
                maxTravelTime,
                maxTransferCount,
                transferDiff,
                timeDiff,
                averageTravelTime,
                timeDiff,
                0.0
        );
    }

    public FairnessScore(
            final int maxTravelTime,
            final int maxTransferCount,
            final int transferDiff,
            final int timeDiff,
            final int averageTravelTime,
            final double lowerSkew,
            final double upperSkew
    ) {
        this.maxTravelTime = maxTravelTime;
        this.maxTransferCount = maxTransferCount;
        this.transferDiff = transferDiff;
        this.timeDiff = timeDiff;
        this.averageTravelTime = averageTravelTime;
        this.lowerSkew = lowerSkew;
        this.upperSkew = upperSkew;
        this.skewRange = Math.max(lowerSkew, upperSkew);
        this.skewImbalance = Math.abs(lowerSkew - upperSkew);
        this.weightedScore = calculateWeightedScore();
    }

    public boolean isAcceptable() {
        return isAcceptable(DispersionPolicy.TIER_1);
    }

    public boolean isAcceptable(final DispersionPolicy dispersionPolicy) {
        return dispersionPolicy.isAcceptable(this);
    }

    @Override
    public int compareTo(final FairnessScore other) {
        final int weightedScoreComparison = Double.compare(weightedScore, other.weightedScore);
        if (weightedScoreComparison != 0) {
            return weightedScoreComparison;
        }
        if (maxTravelTime != other.maxTravelTime) {
            return Integer.compare(maxTravelTime, other.maxTravelTime);
        }
        final int skewRangeComparison = Double.compare(skewRange, other.skewRange);
        if (skewRangeComparison != 0) {
            return skewRangeComparison;
        }
        final int skewImbalanceComparison = Double.compare(skewImbalance, other.skewImbalance);
        if (skewImbalanceComparison != 0) {
            return skewImbalanceComparison;
        }
        if (timeDiff != other.timeDiff) {
            return Integer.compare(timeDiff, other.timeDiff);
        }
        if (maxTransferCount != other.maxTransferCount) {
            return Integer.compare(maxTransferCount, other.maxTransferCount);
        }
        if (transferDiff != other.transferDiff) {
            return Integer.compare(transferDiff, other.transferDiff);
        }
        return Integer.compare(averageTravelTime, other.averageTravelTime);
    }

    private double calculateWeightedScore() {
        return maxTravelTime
                + skewRange * SKEW_RANGE_WEIGHT
                + skewImbalance * SKEW_IMBALANCE_WEIGHT
                + maxTransferCount * TRANSFER_WEIGHT
                + averageTravelTime * AVERAGE_TRAVEL_TIME_WEIGHT;
    }

}
