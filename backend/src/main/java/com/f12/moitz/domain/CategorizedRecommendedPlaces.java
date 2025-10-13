package com.f12.moitz.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class CategorizedRecommendedPlaces {

    private final Map<RecommendCondition, List<RecommendedPlace>> categorizedPlaces;

    public CategorizedRecommendedPlaces(Map<RecommendCondition, List<RecommendedPlace>> categorizedPlaces) {
        if (categorizedPlaces == null || categorizedPlaces.isEmpty()) {
            this.categorizedPlaces = Collections.emptyMap();
        } else {
            this.categorizedPlaces = categorizedPlaces;
        }
    }

    public boolean isEmpty() {
        return categorizedPlaces == null || categorizedPlaces.isEmpty();
    }
}
