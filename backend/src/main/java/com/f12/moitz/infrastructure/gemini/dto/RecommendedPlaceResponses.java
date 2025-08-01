package com.f12.moitz.infrastructure.gemini.dto;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RecommendedPlaceResponses(
        List<RecommendedPlaceResponse> responses
) {
    public List<PlaceRecommendResponse> findPlacesByStationName(String stationName) {
        List<RecommendedSpecificPlace> recommendedSpecificPlaces = responses.stream()
                .filter(response -> stationName.equals(response.stationName()))
                .findFirst()
                .map(RecommendedPlaceResponse::places)
                .get();

        return recommendedSpecificPlaces.stream()
                .map(RecommendedSpecificPlace::convertPlaceRecommendResponse)
                .toList();
    }
}

record RecommendedPlaceResponse(
        String stationName,
        List<RecommendedSpecificPlace> places
) {
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

    PlaceRecommendResponse convertPlaceRecommendResponse() {
        return new PlaceRecommendResponse(
                index,
                name,
                category,
                walkingTime,
                url
        );
    }
}
