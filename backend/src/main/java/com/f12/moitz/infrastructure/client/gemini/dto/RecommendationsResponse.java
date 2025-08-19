package com.f12.moitz.infrastructure.client.gemini.dto;

import java.util.List;

public record RecommendationsResponse(
        List<RecommendedLocation> recommendations
) {

}
