package com.f12.moitz.application.dto;

public record PlaceRecommendResponse(
        int index,
        String name,
        String category,
        int walkingTime,
        String url
) {

}
