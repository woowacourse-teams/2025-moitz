package com.f12.moitz.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FairnessScore implements Comparable<FairnessScore> {

    private static final int MAX_TRAVEL_TIME_LIMIT = 60;
    private static final int MAX_TIME_DIFF_LIMIT = 25;
    private static final int MAX_TRANSFER_LIMIT = 2;

    private final int maxTravelTime;
    private final int maxTransferCount;
    private final int transferDiff;
    private final int timeDiff;
    private final int averageTravelTime;

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
