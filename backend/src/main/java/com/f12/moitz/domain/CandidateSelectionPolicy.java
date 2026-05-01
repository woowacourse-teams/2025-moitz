package com.f12.moitz.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CandidateSelectionPolicy {

    private final RouteCandidateComparators routeCandidateComparators = new RouteCandidateComparators();

    public CandidateSelection select(
            final List<RouteCandidate> candidates,
            final DispersionPolicy dispersionPolicy,
            final int limit
    ) {
        validate(candidates, dispersionPolicy, limit);

        final Map<RouteCandidate, FairnessScore> scoresByCandidate = calculateFairnessScores(candidates);
        final Function<RouteCandidate, FairnessScore> scoreResolver = scoresByCandidate::get;
        final List<RouteCandidate> sortedCandidates = candidates.stream()
                .sorted(Comparator.comparing(scoreResolver))
                .toList();

        for (DispersionPolicy candidatePolicy : dispersionPolicy.relaxations()) {
            final List<RouteCandidate> acceptableCandidates = sortedCandidates.stream()
                    .filter(candidate -> scoreResolver.apply(candidate).isAcceptable(candidatePolicy))
                    .toList();

            if (!acceptableCandidates.isEmpty()) {
                final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = selectTagCandidates(
                        acceptableCandidates,
                        candidatePolicy,
                        scoreResolver
                );
                return new CandidateSelection(
                        mergeTagCandidates(tagSelections, acceptableCandidates, limit),
                        dispersionPolicy,
                        candidatePolicy,
                        acceptableCandidates.size(),
                        false,
                        tagSelections
                );
            }
        }

        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = selectTagCandidates(
                sortedCandidates,
                DispersionPolicy.TIER_5,
                scoreResolver
        );
        return new CandidateSelection(
                mergeTagCandidates(tagSelections, sortedCandidates, limit),
                dispersionPolicy,
                DispersionPolicy.TIER_5,
                0,
                true,
                tagSelections
        );
    }

    private Map<RouteCandidate, FairnessScore> calculateFairnessScores(final List<RouteCandidate> candidates) {
        return candidates.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        RouteCandidate::calculateFairnessScore,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
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

    private Map<CandidateSelectionTag, List<RouteCandidate>> selectTagCandidates(
            final List<RouteCandidate> candidates,
            final DispersionPolicy dispersionPolicy,
            final Function<RouteCandidate, FairnessScore> scoreResolver
    ) {
        final Map<CandidateSelectionTag, Integer> tagQuotas = resolveTagQuotas(dispersionPolicy);
        final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections = new LinkedHashMap<>();

        tagQuotas.forEach((tag, quota) -> tagSelections.put(
                tag,
                candidates.stream()
                        .sorted(routeCandidateComparators.getByTag(tag, scoreResolver))
                        .limit(quota)
                        .toList()
        ));
        return tagSelections;
    }

    private Map<CandidateSelectionTag, Integer> resolveTagQuotas(final DispersionPolicy dispersionPolicy) {
        return switch (dispersionPolicy) {
            case TIER_1, TIER_2 -> createTagQuotas(List.of(
                    CandidateSelectionTag.FAIRNESS,
                    CandidateSelectionTag.MAX_BURDEN_RELIEF,
                    CandidateSelectionTag.EFFICIENCY,
                    CandidateSelectionTag.TRANSFER,
                    CandidateSelectionTag.GENERAL
            ), 7, 6, 12, 5, 5);
            case TIER_3 -> createTagQuotas(List.of(
                    CandidateSelectionTag.FAIRNESS,
                    CandidateSelectionTag.MAX_BURDEN_RELIEF,
                    CandidateSelectionTag.EFFICIENCY,
                    CandidateSelectionTag.TRANSFER,
                    CandidateSelectionTag.GENERAL
            ), 9, 7, 9, 5, 5);
            case TIER_4, TIER_5 -> createTagQuotas(List.of(
                    CandidateSelectionTag.FAIRNESS,
                    CandidateSelectionTag.MAX_BURDEN_RELIEF,
                    CandidateSelectionTag.EFFICIENCY,
                    CandidateSelectionTag.TRANSFER,
                    CandidateSelectionTag.GENERAL
            ), 12, 7, 6, 5, 5);
        };
    }

    private Map<CandidateSelectionTag, Integer> createTagQuotas(
            final List<CandidateSelectionTag> tagOrder,
            final int firstQuota,
            final int secondQuota,
            final int thirdQuota,
            final int fourthQuota,
            final int fifthQuota
    ) {
        final List<Integer> quotas = List.of(firstQuota, secondQuota, thirdQuota, fourthQuota, fifthQuota);
        final Map<CandidateSelectionTag, Integer> tagQuotas = new LinkedHashMap<>();
        for (int index = 0; index < tagOrder.size(); index++) {
            tagQuotas.put(tagOrder.get(index), quotas.get(index));
        }
        return tagQuotas;
    }

    private List<RouteCandidate> mergeTagCandidates(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections,
            final List<RouteCandidate> fallbackCandidates,
            final int limit
    ) {
        final Map<String, RouteCandidate> selectedCandidates = new LinkedHashMap<>();
        final int maxTagSize = tagSelections.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        for (int index = 0; index < maxTagSize && selectedCandidates.size() < limit; index++) {
            for (List<RouteCandidate> tagCandidates : tagSelections.values()) {
                if (index >= tagCandidates.size()) {
                    continue;
                }
                final RouteCandidate candidate = tagCandidates.get(index);
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
