package com.f12.moitz.application;

import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.FinalCandidateSelectionResult;
import com.f12.moitz.domain.Place;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlaceSearchResult {

    private final List<Place> searchedPlaces;
    private final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces;
    private final FinalCandidateSelectionResult finalCandidateSelection;

    public PlaceSearchResult(
            final List<Place> searchedPlaces,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final FinalCandidateSelectionResult finalCandidateSelection
    ) {
        this.searchedPlaces = List.copyOf(searchedPlaces);
        this.recommendedPlaces = Collections.unmodifiableMap(new LinkedHashMap<>(recommendedPlaces));
        this.finalCandidateSelection = finalCandidateSelection;
    }

    public List<Place> getSearchedPlaces() {
        return searchedPlaces;
    }

    public Map<Place, CategorizedRecommendedPlaces> getRecommendedPlaces() {
        return recommendedPlaces;
    }

    public FinalCandidateSelectionResult getFinalCandidateSelection() {
        return finalCandidateSelection;
    }

}
