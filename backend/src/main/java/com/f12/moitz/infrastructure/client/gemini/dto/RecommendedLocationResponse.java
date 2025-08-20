package com.f12.moitz.infrastructure.client.gemini.dto;

public record RecommendedLocationResponse(
        String locationName,
        String reason,
        String description
) {

}
