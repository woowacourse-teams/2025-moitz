package com.f12.moitz.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlaceSearchCandidateSelector {

    private final RouteCandidateComparators routeCandidateComparators = new RouteCandidateComparators();

    public CandidateSelectionResult select(
            final List<RouteCandidate> candidates,
            final DispersionPolicy dispersionPolicy,
            final int limit
    ) {
        validate(candidates, dispersionPolicy, limit);

        final List<RouteCandidate> sortedCandidates = candidates.stream()
                .sorted((left, right) -> left.calculateFairnessScore().compareTo(right.calculateFairnessScore()))
                .toList();

        for (DispersionPolicy candidatePolicy : dispersionPolicy.relaxations()) {
            final List<RouteCandidate> acceptableCandidates = sortedCandidates.stream()
                    .filter(candidate -> candidate.isAcceptable(candidatePolicy))
                    .toList();

            if (!acceptableCandidates.isEmpty()) {
                final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections = selectBucketCandidates(
                        acceptableCandidates,
                        candidatePolicy
                );
                return new CandidateSelectionResult(
                        mergeBucketCandidates(bucketSelections, acceptableCandidates, limit),
                        dispersionPolicy,
                        candidatePolicy,
                        acceptableCandidates.size(),
                        false,
                        bucketSelections
                );
            }
        }

        final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections = selectBucketCandidates(
                sortedCandidates,
                DispersionPolicy.TIER_5
        );
        return new CandidateSelectionResult(
                mergeBucketCandidates(bucketSelections, sortedCandidates, limit),
                dispersionPolicy,
                DispersionPolicy.TIER_5,
                0,
                true,
                bucketSelections
        );
    }

    private void validate(
            final List<RouteCandidate> candidates,
            final DispersionPolicy dispersionPolicy,
            final int limit
    ) {
        if (candidates == null) {
            throw new IllegalArgumentException("후보 목록은 null일 수 없습니다.");
        }
        if (dispersionPolicy == null) {
            throw new IllegalArgumentException("출발지 분산도 정책은 필수입니다.");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("후보 선발 개수는 1 이상이어야 합니다.");
        }
    }

    private Map<CandidateSelectionBucket, List<RouteCandidate>> selectBucketCandidates(
            final List<RouteCandidate> candidates,
            final DispersionPolicy dispersionPolicy
    ) {
        final Map<CandidateSelectionBucket, Integer> bucketQuotas = resolveBucketQuotas(dispersionPolicy);
        final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections = new LinkedHashMap<>();

        bucketQuotas.forEach((bucket, quota) -> bucketSelections.put(
                bucket,
                candidates.stream()
                        .sorted(routeCandidateComparators.getByBucket(bucket))
                        .limit(quota)
                        .toList()
        ));
        return bucketSelections;
    }

    private Map<CandidateSelectionBucket, Integer> resolveBucketQuotas(final DispersionPolicy dispersionPolicy) {
        return switch (dispersionPolicy) {
            case TIER_1, TIER_2 -> createBucketQuotas(List.of(
                    CandidateSelectionBucket.EFFICIENCY,
                    CandidateSelectionBucket.FAIRNESS,
                    CandidateSelectionBucket.MAX_BURDEN_RELIEF,
                    CandidateSelectionBucket.TRANSFER
            ), 12, 7, 6, 5);
            case TIER_3 -> createBucketQuotas(List.of(
                    CandidateSelectionBucket.FAIRNESS,
                    CandidateSelectionBucket.EFFICIENCY,
                    CandidateSelectionBucket.MAX_BURDEN_RELIEF,
                    CandidateSelectionBucket.TRANSFER
            ), 9, 9, 7, 5);
            case TIER_4, TIER_5 -> createBucketQuotas(List.of(
                    CandidateSelectionBucket.FAIRNESS,
                    CandidateSelectionBucket.MAX_BURDEN_RELIEF,
                    CandidateSelectionBucket.TRANSFER,
                    CandidateSelectionBucket.EFFICIENCY
            ), 12, 7, 5, 6);
        };
    }

    private Map<CandidateSelectionBucket, Integer> createBucketQuotas(
            final List<CandidateSelectionBucket> bucketOrder,
            final int firstQuota,
            final int secondQuota,
            final int thirdQuota,
            final int fourthQuota
    ) {
        final List<Integer> quotas = List.of(firstQuota, secondQuota, thirdQuota, fourthQuota);
        final Map<CandidateSelectionBucket, Integer> bucketQuotas = new LinkedHashMap<>();
        for (int index = 0; index < bucketOrder.size(); index++) {
            bucketQuotas.put(bucketOrder.get(index), quotas.get(index));
        }
        return bucketQuotas;
    }

    private List<RouteCandidate> mergeBucketCandidates(
            final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections,
            final List<RouteCandidate> fallbackCandidates,
            final int limit
    ) {
        final Map<String, RouteCandidate> selectedCandidates = new LinkedHashMap<>();
        final int maxBucketSize = bucketSelections.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        for (int index = 0; index < maxBucketSize && selectedCandidates.size() < limit; index++) {
            for (List<RouteCandidate> bucketCandidates : bucketSelections.values()) {
                if (index >= bucketCandidates.size()) {
                    continue;
                }
                final RouteCandidate candidate = bucketCandidates.get(index);
                selectedCandidates.putIfAbsent(candidate.getPlace().getName(), candidate);
                if (selectedCandidates.size() >= limit) {
                    break;
                }
            }
        }

        for (RouteCandidate fallbackCandidate : fallbackCandidates) {
            if (selectedCandidates.size() >= limit) {
                break;
            }
            selectedCandidates.putIfAbsent(fallbackCandidate.getPlace().getName(), fallbackCandidate);
        }

        return new ArrayList<>(selectedCandidates.values());
    }

}
