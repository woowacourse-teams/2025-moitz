package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

@Getter
public class CategorizedRecommendedPlaces {

    private final Map<RecommendCondition, List<RecommendedPlace>> categorizedPlaces;

    public CategorizedRecommendedPlaces(Map<RecommendCondition, List<RecommendedPlace>> categorizedPlaces) {
        if (categorizedPlaces == null || categorizedPlaces.isEmpty()) {
            this.categorizedPlaces = Map.of();
        } else {
            validate(categorizedPlaces);
            this.categorizedPlaces = copyOf(categorizedPlaces);
        }
    }

    public boolean isEmpty() {
        return categorizedPlaces.isEmpty();
    }

    public boolean satisfiesAll(final List<RecommendCondition> recommendConditions) {
        if (recommendConditions == null || recommendConditions.isEmpty()) {
            return false;
        }
        return recommendConditions.stream()
                .allMatch(this::hasRecommendedPlaces);
    }

    private boolean hasRecommendedPlaces(final RecommendCondition recommendCondition) {
        return categorizedPlaces.containsKey(recommendCondition)
                && !categorizedPlaces.get(recommendCondition).isEmpty();
    }

    private void validate(final Map<RecommendCondition, List<RecommendedPlace>> categorizedPlaces) {
        if (categorizedPlaces.keySet().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 조건은 null일 수 없습니다.");
        }
        if (categorizedPlaces.values().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 장소 목록은 null일 수 없습니다.");
        }
        if (categorizedPlaces.values().stream()
                .flatMap(List::stream)
                .anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 장소 목록에 null이 포함될 수 없습니다.");
        }
    }

    private Map<RecommendCondition, List<RecommendedPlace>> copyOf(
            final Map<RecommendCondition, List<RecommendedPlace>> categorizedPlaces
    ) {
        final Map<RecommendCondition, List<RecommendedPlace>> copiedCategorizedPlaces = new LinkedHashMap<>();
        categorizedPlaces.forEach((recommendCondition, recommendedPlaces) ->
                copiedCategorizedPlaces.put(recommendCondition, List.copyOf(recommendedPlaces))
        );
        return Collections.unmodifiableMap(copiedCategorizedPlaces);
    }
}
