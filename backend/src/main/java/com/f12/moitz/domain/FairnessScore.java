package com.f12.moitz.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class FairnessScore implements Comparable<FairnessScore> {

    private final int maxTravelTime;
    private final int maxTransferCount;
    private final int transferDiff;
    private final int timeDiff;
    private final int averageTravelTime;

    public boolean isAcceptable() {
        return isAcceptable(DispersionPolicy.TIER_1);
    }

    public boolean isAcceptable(final DispersionPolicy dispersionPolicy) {
        return dispersionPolicy.isAcceptable(this);
    }

    @Override
    public int compareTo(final FairnessScore other) {
        if (maxTravelTime != other.maxTravelTime) {
            return Integer.compare(maxTravelTime, other.maxTravelTime);
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

}
