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
        final Map<CandidateSelectionTag, Integer> tagQuotas = dispersionPolicy.tagQuotas();
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

    private List<RouteCandidate> mergeTagCandidates(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections,
            final List<RouteCandidate> fallbackCandidates,
            final int limit
    ) {
        final Map<String, RouteCandidate> searchCandidates = new LinkedHashMap<>();
        final int maxTagSize = tagSelections.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        for (int index = 0; index < maxTagSize && searchCandidates.size() < limit; index++) {
            for (List<RouteCandidate> tagCandidates : tagSelections.values()) {
                if (index >= tagCandidates.size()) {
                    continue;
                }
                final RouteCandidate candidate = tagCandidates.get(index);
                searchCandidates.putIfAbsent(candidate.getPlace().getName(), candidate);
                if (searchCandidates.size() >= limit) {
                    break;
                }
            }
        }

        for (RouteCandidate fallbackCandidate : fallbackCandidates) {
            if (searchCandidates.size() >= limit) {
                break;
            }
            searchCandidates.putIfAbsent(fallbackCandidate.getPlace().getName(), fallbackCandidate);
        }

        return new ArrayList<>(searchCandidates.values());
    }

}
