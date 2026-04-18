package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class CandidateSelectionResult {

    private final List<RouteCandidate> selectedCandidates;
    private final DispersionPolicy initialPolicy;
    private final DispersionPolicy effectivePolicy;
    private final long acceptableCount;
    private final boolean fallbackToSortedCandidates;
    private final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections;

    public CandidateSelectionResult(
            final List<RouteCandidate> selectedCandidates,
            final DispersionPolicy initialPolicy,
            final DispersionPolicy effectivePolicy,
            final long acceptableCount,
            final boolean fallbackToSortedCandidates,
            final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections
    ) {
        this.selectedCandidates = List.copyOf(selectedCandidates);
        this.initialPolicy = initialPolicy;
        this.effectivePolicy = effectivePolicy;
        this.acceptableCount = acceptableCount;
        this.fallbackToSortedCandidates = fallbackToSortedCandidates;
        this.bucketSelections = copyBucketSelections(bucketSelections);
    }

    private Map<CandidateSelectionBucket, List<RouteCandidate>> copyBucketSelections(
            final Map<CandidateSelectionBucket, List<RouteCandidate>> bucketSelections
    ) {
        return Collections.unmodifiableMap(bucketSelections.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                )));
    }

    public List<Place> getSelectedPlaces() {
        return selectedCandidates.stream()
                .map(RouteCandidate::getPlace)
                .toList();
    }

}
