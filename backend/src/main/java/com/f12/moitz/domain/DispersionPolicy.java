package com.f12.moitz.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum DispersionPolicy {

    TIER_1(3, 45, 30.0, 0, 60, 20, 2, 10, false),
    TIER_2(4, 70, 45.0, 0, 65, 25, 2, 10, false),
    TIER_3(6, 110, 75.0, 3, 70, 35, 3, 20, false),
    TIER_4(6, 140, 90.0, 5, 75, 47, 3, 30, false),
    TIER_5(6, 0, 0.0, 0, 95, 68, 3, 40, true);

    public static final int MIN_STARTING_PLACE_COUNT = 2;
    public static final int MAX_STARTING_PLACE_COUNT = 6;
    public static final int LONG_PAIR_TRAVEL_TIME_MINUTES = 90;

    private final int maxPartySize;
    private final int maxPairTravelTime;
    private final double maxAveragePairTravelTime;
    private final long maxLongPairCount;
    private final int maxTravelTimeLimit;
    private final int timeDiffLimit;
    private final int maxTransferLimit;
    private final int candidateSearchRadiusKilometers;
    private final boolean fallback;

    DispersionPolicy(
            final int maxPartySize,
            final int maxPairTravelTime,
            final double maxAveragePairTravelTime,
            final long maxLongPairCount,
            final int maxTravelTimeLimit,
            final int timeDiffLimit,
            final int maxTransferLimit,
            final int candidateSearchRadiusKilometers,
            final boolean fallback
    ) {
        this.maxPartySize = maxPartySize;
        this.maxPairTravelTime = maxPairTravelTime;
        this.maxAveragePairTravelTime = maxAveragePairTravelTime;
        this.maxLongPairCount = maxLongPairCount;
        this.maxTravelTimeLimit = maxTravelTimeLimit;
        this.timeDiffLimit = timeDiffLimit;
        this.maxTransferLimit = maxTransferLimit;
        this.candidateSearchRadiusKilometers = candidateSearchRadiusKilometers;
        this.fallback = fallback;
    }

    public static DispersionPolicy resolve(
            final int partySize,
            final int pairMaxTravelTime,
            final double pairAverageTravelTime,
            final long longPairCount
    ) {
        if (partySize < MIN_STARTING_PLACE_COUNT || partySize > MAX_STARTING_PLACE_COUNT) {
            throw new IllegalArgumentException(
                    "출발지는 " + MIN_STARTING_PLACE_COUNT + "개 이상 " + MAX_STARTING_PLACE_COUNT + "개 이하로 입력해야 합니다."
            );
        }
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

    public Map<CandidateSelectionTag, Integer> tagQuotas() {
        return switch (this) {
            case TIER_1, TIER_2 -> createTagQuotas(7, 6, 12, 5, 5);
            case TIER_3 -> createTagQuotas(9, 7, 9, 5, 5);
            case TIER_4, TIER_5 -> createTagQuotas(12, 7, 6, 5, 5);
        };
    }

    public int candidateSearchRadiusKilometers() {
        return candidateSearchRadiusKilometers;
    }

    private Map<CandidateSelectionTag, Integer> createTagQuotas(
            final int fairnessQuota,
            final int maxBurdenReliefQuota,
            final int efficiencyQuota,
            final int transferQuota,
            final int generalQuota
    ) {
        final Map<CandidateSelectionTag, Integer> tagQuotas = new LinkedHashMap<>();
        tagQuotas.put(CandidateSelectionTag.FAIRNESS, fairnessQuota);
        tagQuotas.put(CandidateSelectionTag.MAX_BURDEN_RELIEF, maxBurdenReliefQuota);
        tagQuotas.put(CandidateSelectionTag.EFFICIENCY, efficiencyQuota);
        tagQuotas.put(CandidateSelectionTag.TRANSFER, transferQuota);
        tagQuotas.put(CandidateSelectionTag.GENERAL, generalQuota);
        return Collections.unmodifiableMap(tagQuotas);
    }

}
