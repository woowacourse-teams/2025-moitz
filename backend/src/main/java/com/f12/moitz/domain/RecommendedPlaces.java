package com.f12.moitz.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;

@Getter
public class RecommendedPlaces {

    private final Map<RecommendCondition, List<RecommendedPlace>> placesByCondition;

    public RecommendedPlaces(final Map<RecommendCondition, List<RecommendedPlace>> placesByCondition) {
        if (placesByCondition == null || placesByCondition.isEmpty()) {
            this.placesByCondition = Map.of();
        } else {
            validate(placesByCondition);
            this.placesByCondition = copyOf(placesByCondition);
        }
    }

    public boolean isEmpty() {
        return placesByCondition.isEmpty();
    }

    public boolean satisfiesAllConditions(final List<RecommendCondition> recommendConditions) {
        if (recommendConditions == null || recommendConditions.isEmpty()) {
            return false;
        }
        return recommendConditions.stream()
                .allMatch(this::hasRecommendedPlaces);
    }

    private boolean hasRecommendedPlaces(final RecommendCondition recommendCondition) {
        return placesByCondition.containsKey(recommendCondition)
                && !placesByCondition.get(recommendCondition).isEmpty();
    }

    private void validate(final Map<RecommendCondition, List<RecommendedPlace>> placesByCondition) {
        if (placesByCondition.keySet().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 조건은 null일 수 없습니다.");
        }
        if (placesByCondition.values().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 장소 목록은 null일 수 없습니다.");
        }
        if (placesByCondition.values().stream()
                .flatMap(List::stream)
                .anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 장소 목록에 null이 포함될 수 없습니다.");
        }
    }

    private Map<RecommendCondition, List<RecommendedPlace>> copyOf(
            final Map<RecommendCondition, List<RecommendedPlace>> placesByCondition
    ) {
        final Map<RecommendCondition, List<RecommendedPlace>> copiedPlacesByCondition = new LinkedHashMap<>();
        placesByCondition.forEach((recommendCondition, recommendedPlaces) ->
                copiedPlacesByCondition.put(recommendCondition, List.copyOf(recommendedPlaces))
        );
        return Collections.unmodifiableMap(copiedPlacesByCondition);
    }
}
