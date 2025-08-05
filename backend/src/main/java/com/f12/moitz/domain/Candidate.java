package com.f12.moitz.domain;

import java.util.List;
import lombok.Getter;

@Getter
public class Candidate {

    private final Place destination;
    private final Routes routes;
    private final List<RecommendedPlace> recommendedPlaces;

    public Candidate(final Place destination, final Routes routes, final List<RecommendedPlace> recommendedPlaces) {
        validate(destination, routes, recommendedPlaces);
        this.destination = destination;
        this.routes = routes;
        this.recommendedPlaces = recommendedPlaces;
    }

    private void validate(final Place suggestedLocation, final Routes routes, final List<RecommendedPlace> recommendedPlaces) {
        if (suggestedLocation == null) {
            throw new IllegalArgumentException("추천 지역은 필수입니다.");
        }
        if (routes == null) {
            throw new IllegalArgumentException("경로 목록은 필수입니다.");
        }
        if (recommendedPlaces == null || recommendedPlaces.isEmpty()) {
            throw new IllegalArgumentException("추천 장소 목록은 비어 있을 수 없습니다.");
        }
    }

    public int calculateAverageTravelTime() {
        return routes.calculateAverageTravelTime();
    }



}
