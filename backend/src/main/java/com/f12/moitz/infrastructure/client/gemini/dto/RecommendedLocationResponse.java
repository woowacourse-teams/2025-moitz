package com.f12.moitz.infrastructure.client.gemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RecommendedLocationResponse(
        String locationName,
        @JsonProperty("detail_reason") String reason,
        @JsonProperty("summarize_reason")String description
) {

}
