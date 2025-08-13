package com.f12.moitz.infrastructure.gemini.dto;

import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RecommendedPlaceResponses(
        List<RecommendedPlaceResponse> responses
) {

    public Map<String, List<PlaceRecommendResponse>> getPlacesByStationName() {
        return responses.stream()
                .map(response -> Map.entry(
                        response.stationName(),
                        response.toPlaceRecommendResponses()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

record RecommendedPlaceResponse(
        String stationName,
        List<RecommendedSpecificPlace> places
) {
    List<PlaceRecommendResponse> toPlaceRecommendResponses() {
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
