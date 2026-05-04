package com.f12.moitz.domain.recommendation.candidate;

import com.f12.moitz.domain.Place;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CandidateSelection {

    private final List<RouteCandidate> searchCandidates;
    private final DispersionPolicy initialPolicy;
    private final DispersionPolicy effectivePolicy;
    private final long acceptableCount;
    private final boolean fallbackToSortedCandidates;
    private final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections;

    public static CandidateSelection fromTagSelections(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections,
            final List<RouteCandidate> supplementaryCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final boolean fallbackToSortedCandidates,
            final int limit
    ) {
        return new CandidateSelection(
                tagSelections,
                supplementaryCandidates,
                initialPolicy,
                effectivePolicy,
                acceptableCount,
                fallbackToSortedCandidates,
                limit
        );
    }

    public CandidateSelection(
            final List<RouteCandidate> searchCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final boolean fallbackToSortedCandidates,
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections
    ) {
        validate(searchCandidates, initialPolicy, effectivePolicy, acceptableCount, tagSelections);
        this.searchCandidates = List.copyOf(searchCandidates);
        this.initialPolicy = initialPolicy;
        this.effectivePolicy = effectivePolicy;
        this.acceptableCount = acceptableCount;
        this.fallbackToSortedCandidates = fallbackToSortedCandidates;
        this.tagSelections = copyTagSelections(tagSelections);
    }

    private CandidateSelection(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections,
            final List<RouteCandidate> supplementaryCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final boolean fallbackToSortedCandidates,
            final int limit
    ) {
        validateTagSelectionInputs(
                tagSelections,
                supplementaryCandidates,
                initialPolicy,
                effectivePolicy,
                acceptableCount,
                limit
        );
        this.searchCandidates = composeSearchCandidates(tagSelections, supplementaryCandidates, limit);
        this.initialPolicy = initialPolicy;
        this.effectivePolicy = effectivePolicy;
        this.acceptableCount = acceptableCount;
        this.fallbackToSortedCandidates = fallbackToSortedCandidates;
        this.tagSelections = copyTagSelections(tagSelections);
    }

    private void validate(
            final List<RouteCandidate> searchCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections
    ) {
        if (searchCandidates == null) {
            throw new IllegalArgumentException("검색 후보 목록은 null일 수 없습니다.");
        }
        validateCommonInputs(initialPolicy, effectivePolicy, acceptableCount, tagSelections);
    }

    private void validateTagSelectionInputs(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections,
            final List<RouteCandidate> supplementaryCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final int limit
    ) {
        validateCommonInputs(initialPolicy, effectivePolicy, acceptableCount, tagSelections);
        if (supplementaryCandidates == null) {
            throw new IllegalArgumentException("보충 후보 목록은 null일 수 없습니다.");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("후보 선발 개수는 1 이상이어야 합니다.");
        }
    }

    private void validateCommonInputs(
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections
    ) {
        if (initialPolicy == null) {
            throw new IllegalArgumentException("초기 분산도 정책은 필수입니다.");
        }
        if (effectivePolicy == null) {
            throw new IllegalArgumentException("적용 분산도 정책은 필수입니다.");
        }
        if (acceptableCount < 0) {
            throw new IllegalArgumentException("허용 후보 수는 음수일 수 없습니다.");
        }
        if (tagSelections == null) {
            throw new IllegalArgumentException("태그별 후보 목록은 null일 수 없습니다.");
        }
    }

    private List<RouteCandidate> composeSearchCandidates(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections,
            final List<RouteCandidate> supplementaryCandidates,
            final int limit
    ) {
        final Map<Place, RouteCandidate> searchCandidates = new LinkedHashMap<>();
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
                searchCandidates.putIfAbsent(candidate.getPlace(), candidate);
                if (searchCandidates.size() >= limit) {
                    break;
                }
            }
        }

        for (RouteCandidate supplementaryCandidate : supplementaryCandidates) {
            if (searchCandidates.size() >= limit) {
                break;
            }
            searchCandidates.putIfAbsent(supplementaryCandidate.getPlace(), supplementaryCandidate);
        }

        return new ArrayList<>(searchCandidates.values());
    }

    private Map<CandidateSelectionTag, List<RouteCandidate>> copyTagSelections(
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections
    ) {
        return Collections.unmodifiableMap(tagSelections.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                )));
    }

    public List<RouteCandidate> getSearchCandidates() {
        return searchCandidates;
    }

    public List<Place> getSearchCandidatePlaces() {
        return searchCandidates.stream()
                .map(RouteCandidate::getPlace)
                .toList();
    }

    public List<RouteCandidate> getCandidatesByTag(final CandidateSelectionTag tag) {
        return tagSelections.getOrDefault(tag, List.of());
    }

    public DispersionPolicy getInitialPolicy() {
        return initialPolicy;
    }

    public DispersionPolicy getEffectivePolicy() {
        return effectivePolicy;
    }

    public long getAcceptableCount() {
        return acceptableCount;
    }

    public boolean isFallbackToSortedCandidates() {
        return fallbackToSortedCandidates;
    }

}
