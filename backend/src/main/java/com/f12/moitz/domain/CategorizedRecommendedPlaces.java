package com.f12.moitz.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CategorizedRecommendedPlaces {

    private final Map<String, List<RecommendedPlace>> categorizedPlaces;

    public static CategorizedRecommendedPlaces from(final Map<String, List<RecommendedPlace>> categorizedPlaces) {
        if (categorizedPlaces == null || categorizedPlaces.isEmpty()) {
            return new CategorizedRecommendedPlaces(Collections.emptyMap());
        }
        return new CategorizedRecommendedPlaces(categorizedPlaces);
    }

    public boolean isEmpty() {
        return categorizedPlaces == null || categorizedPlaces.isEmpty();
    }
}
