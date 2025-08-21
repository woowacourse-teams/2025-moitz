package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Candidate {

    private final Place destination;
    private final Routes routes;
    private final List<RecommendedPlace> recommendedPlaces;
    private final String description;
    private final String reason;

    public Candidate(
            final Place destination,
            final Routes routes,
            final List<RecommendedPlace> recommendedPlaces,
            final String description,
            final String reason
    ) {
        validate(destination, routes, recommendedPlaces, description, reason);
        this.destination = destination;
        this.routes = routes;
        this.recommendedPlaces = recommendedPlaces;
        this.description = description;
        this.reason = reason;
    }

    private void validate(
            final Place suggestedLocation,
            final Routes routes,
            final List<RecommendedPlace> recommendedPlaces,
            final String description,
            final String reason
    ) {
        if (suggestedLocation == null) {
            throw new IllegalArgumentException("추천 지역은 필수입니다.");
        }
        if (routes == null) {
            throw new IllegalArgumentException("경로 목록은 필수입니다.");
        }
        if (recommendedPlaces == null || recommendedPlaces.isEmpty()) {
            throw new IllegalArgumentException("추천 장소 목록은 비어 있을 수 없습니다.");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("추천 설명은 비어 있을 수 없습니다.");
        }
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("추천 이유는 비어 있을 수 없습니다.");
        }
    }

    public int calculateAverageTravelTime() {
        return routes.calculateAverageTravelTime();
    }

}
