package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class RecommendedCandidates {

    private final List<Place> recommendedCandidatePlaces;
    private final Map<Place, List<CandidateSelectionTag>> tagsByPlace;

    public RecommendedCandidates(
            final List<Place> recommendedCandidatePlaces,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {
        this.recommendedCandidatePlaces = List.copyOf(recommendedCandidatePlaces);
        this.tagsByPlace = copyTagsByPlace(tagsByPlace);
    }

    public CandidateSelectionTag getTag(final Place place) {
        return getTags(place).get(0);
    }

    public List<CandidateSelectionTag> getTags(final Place place) {
        return CandidateSelectionTag.normalize(tagsByPlace.get(place));
    }

    private Map<Place, List<CandidateSelectionTag>> copyTagsByPlace(
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace
    ) {
        return Collections.unmodifiableMap(tagsByPlace.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> CandidateSelectionTag.normalize(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                )));
    }

}
