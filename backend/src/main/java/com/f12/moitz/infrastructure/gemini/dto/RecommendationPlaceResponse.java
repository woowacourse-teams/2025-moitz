package com.f12.moitz.infrastructure.gemini.dto;

import java.util.List;

public record RecommendationPlaceResponse(
        List<StationRecommendation> stationRecommendations
) {

}

record StationRecommendation(
        String stationName,
        List<RecommendedPlace> places
) {

}

record RecommendedPlace(
        String name,
        String category,
        String description,
        String address,
        double latitude,
        double longitude
) {

}
