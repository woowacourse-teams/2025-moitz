package com.f12.moitz.application;

import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.Place;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecommendationPlaceSearchResult {

    private final List<Place> searchedPlaces;
    private final Map<Place, RecommendedPlaces> recommendedPlaces;
    private final RecommendedCandidates recommendedCandidates;

    public RecommendationPlaceSearchResult(
            final List<Place> searchedPlaces,
            final Map<Place, RecommendedPlaces> recommendedPlaces,
            final RecommendedCandidates recommendedCandidates
    ) {
        this.searchedPlaces = List.copyOf(searchedPlaces);
        this.recommendedPlaces = Collections.unmodifiableMap(new LinkedHashMap<>(recommendedPlaces));
        this.recommendedCandidates = recommendedCandidates;
    }

    public List<Place> getSearchedPlaces() {
        return searchedPlaces;
    }

    public int getSearchedPlaceCount() {
        return searchedPlaces.size();
    }

    public Map<Place, RecommendedPlaces> getRecommendedPlaces() {
        return recommendedPlaces;
    }

    public int getRecommendedPlaceCount() {
        return recommendedPlaces.size();
    }

    public RecommendedCandidates getRecommendedCandidates() {
        return recommendedCandidates;
    }

}
