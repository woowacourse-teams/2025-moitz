package com.f12.moitz.application;

import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.SelectedCandidates;
import com.f12.moitz.domain.Place;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecommendationPlaceSearchResult {

    private final List<Place> searchedPlaces;
    private final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces;
    private final SelectedCandidates selectedCandidates;

    public RecommendationPlaceSearchResult(
            final List<Place> searchedPlaces,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final SelectedCandidates selectedCandidates
    ) {
        this.searchedPlaces = List.copyOf(searchedPlaces);
        this.recommendedPlaces = Collections.unmodifiableMap(new LinkedHashMap<>(recommendedPlaces));
        this.selectedCandidates = selectedCandidates;
    }

    public List<Place> getSearchedPlaces() {
        return searchedPlaces;
    }

    public Map<Place, CategorizedRecommendedPlaces> getRecommendedPlaces() {
        return recommendedPlaces;
    }

    public SelectedCandidates getSelectedCandidates() {
        return selectedCandidates;
    }

}
