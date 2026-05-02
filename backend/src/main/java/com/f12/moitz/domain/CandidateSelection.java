package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CandidateSelection {

    private final List<RouteCandidate> searchCandidates;
    private final DispersionPolicy initialPolicy;
    private final DispersionPolicy effectivePolicy;
    private final long acceptableCount;
    private final boolean fallbackToSortedCandidates;
    private final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections;

    public CandidateSelection(
            final List<RouteCandidate> searchCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final boolean fallbackToSortedCandidates,
            final Map<CandidateSelectionTag, List<RouteCandidate>> tagSelections
    ) {
        this.searchCandidates = List.copyOf(searchCandidates);
        this.initialPolicy = initialPolicy;
        this.effectivePolicy = effectivePolicy;
        this.acceptableCount = acceptableCount;
        this.fallbackToSortedCandidates = fallbackToSortedCandidates;
        this.tagSelections = copyTagSelections(tagSelections);
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

    public List<Place> getSearchCandidatePlaces() {
        return searchCandidates.stream()
                .map(RouteCandidate::getPlace)
                .toList();
    }

}
