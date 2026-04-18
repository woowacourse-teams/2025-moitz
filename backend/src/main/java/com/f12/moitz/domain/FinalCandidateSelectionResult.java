package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class FinalCandidateSelectionResult {

    private final List<Place> selectedPlaces;
    private final Map<Place, CandidateSelectionTag> tagsByPlace;

    public FinalCandidateSelectionResult(
            final List<Place> selectedPlaces,
            final Map<Place, CandidateSelectionTag> tagsByPlace
    ) {
        this.selectedPlaces = List.copyOf(selectedPlaces);
        this.tagsByPlace = copyTagsByPlace(tagsByPlace);
    }

    public CandidateSelectionTag getTag(final Place place) {
        return tagsByPlace.getOrDefault(place, CandidateSelectionTag.GENERAL);
    }

    private Map<Place, CandidateSelectionTag> copyTagsByPlace(final Map<Place, CandidateSelectionTag> tagsByPlace) {
        return Collections.unmodifiableMap(tagsByPlace.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                )));
    }

}
