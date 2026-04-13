package com.f12.moitz.domain;

public record FairnessScore(
        int maxTravelTime,
        int maxTransferCount,
        int transferDiff,
        int timeDiff,
        int averageTravelTime
) implements Comparable<FairnessScore> {

    private static final int MAX_TRAVEL_TIME_LIMIT = 60;
    private static final int MAX_TIME_DIFF_LIMIT = 25;
    private static final int MAX_TRANSFER_LIMIT = 2;

    public boolean isAcceptable() {
        return maxTravelTime <= MAX_TRAVEL_TIME_LIMIT
                && timeDiff <= MAX_TIME_DIFF_LIMIT
                && maxTransferCount <= MAX_TRANSFER_LIMIT;
    }

    @Override
    public int compareTo(final FairnessScore other) {
        if (maxTravelTime != other.maxTravelTime) {
            return Integer.compare(maxTravelTime, other.maxTravelTime);
        }
        if (maxTransferCount != other.maxTransferCount) {
            return Integer.compare(maxTransferCount, other.maxTransferCount);
        }
        if (transferDiff != other.transferDiff) {
            return Integer.compare(transferDiff, other.transferDiff);
        }
        if (timeDiff != other.timeDiff) {
            return Integer.compare(timeDiff, other.timeDiff);
        }
        return Integer.compare(averageTravelTime, other.averageTravelTime);
    }
}
