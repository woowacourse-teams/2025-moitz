package com.f12.moitz.domain;

import java.util.Arrays;
import java.util.List;

public enum DispersionPolicy {

    TIER_1(3, 45, 30.0, 0, 60, 20, 2, false),
    TIER_2(4, 70, 45.0, 0, 65, 25, 2, false),
    TIER_3(6, 110, 75.0, 3, 70, 35, 3, false),
    TIER_4(6, 140, 90.0, 5, 75, 47, 3, false),
    TIER_5(6, 0, 0.0, 0, 95, 68, 3, true);

    public static final int MAX_STARTING_PLACE_COUNT = 6;
    public static final int LONG_PAIR_TRAVEL_TIME_MINUTES = 90;

    private final int maxPartySize;
    private final int maxPairTravelTime;
    private final double maxAveragePairTravelTime;
    private final long maxLongPairCount;
    private final int maxTravelTimeLimit;
    private final int timeDiffLimit;
    private final int maxTransferLimit;
    private final boolean fallback;

    DispersionPolicy(
            final int maxPartySize,
            final int maxPairTravelTime,
            final double maxAveragePairTravelTime,
            final long maxLongPairCount,
            final int maxTravelTimeLimit,
            final int timeDiffLimit,
            final int maxTransferLimit,
            final boolean fallback
    ) {
        this.maxPartySize = maxPartySize;
        this.maxPairTravelTime = maxPairTravelTime;
        this.maxAveragePairTravelTime = maxAveragePairTravelTime;
        this.maxLongPairCount = maxLongPairCount;
        this.maxTravelTimeLimit = maxTravelTimeLimit;
        this.timeDiffLimit = timeDiffLimit;
        this.maxTransferLimit = maxTransferLimit;
        this.fallback = fallback;
    }

    public static DispersionPolicy resolve(
            final int partySize,
            final int pairMaxTravelTime,
            final double pairAverageTravelTime,
            final long longPairCount
    ) {
        validatePartySize(partySize);
        return Arrays.stream(values())
                .filter(policy -> !policy.fallback)
                .filter(policy -> policy.supports(
                        partySize,
                        pairMaxTravelTime,
                        pairAverageTravelTime,
                        longPairCount
                ))
                .findFirst()
                .orElse(TIER_5);
    }

    private static void validatePartySize(final int partySize) {
        if (partySize <= 0 || partySize > MAX_STARTING_PLACE_COUNT) {
            throw new IllegalArgumentException("출발지는 1개 이상 " + MAX_STARTING_PLACE_COUNT + "개 이하로 입력해야 합니다.");
        }
    }

    private boolean supports(
            final int partySize,
            final int pairMaxTravelTime,
            final double pairAverageTravelTime,
            final long longPairCount
    ) {
        return partySize <= maxPartySize
                && pairMaxTravelTime <= maxPairTravelTime
                && pairAverageTravelTime <= maxAveragePairTravelTime
                && longPairCount <= maxLongPairCount;
    }

    public boolean isAcceptable(final FairnessScore score) {
        return score.getMaxTravelTime() <= maxTravelTimeLimit
                && score.getTimeDiff() <= timeDiffLimit
                && score.getMaxTransferCount() <= maxTransferLimit;
    }

    public List<DispersionPolicy> relaxations() {
        return Arrays.stream(values())
                .filter(policy -> policy.ordinal() >= ordinal())
                .toList();
    }

}
