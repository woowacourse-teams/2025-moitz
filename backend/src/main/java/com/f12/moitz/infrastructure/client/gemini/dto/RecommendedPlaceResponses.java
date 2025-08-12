package com.f12.moitz.infrastructure.client.gemini.dto;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendedPlaceResponses(
        @JsonProperty("places") List<RecommendedSpecificPlace> places
) {

    public List<PlaceRecommendResponse> getPlacesByStationName() {
        if (places == null) {
            return List.of();
        }

        return places.stream()
                .map(RecommendedSpecificPlace::toPlaceRecommendResponse)
                .toList();
    }
}

record RecommendedSpecificPlace(
        int index,
        String name,
        String category,
        @JsonProperty("distance") int walkingTime,
        String url
) {
    public RecommendedSpecificPlace(final int index, final String name, final String category, final int walkingTime,
                                    final String url) {
        this.index = index;
        this.name = name;
        this.category = category;
        this.walkingTime = (int) Math.round(walkingTime / 80.0);
        this.url = url;
    }

    PlaceRecommendResponse toPlaceRecommendResponse() {
        return new PlaceRecommendResponse(
                index,
                name,
                category,
                walkingTime,
                url
        );
    }
}