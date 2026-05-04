package com.f12.moitz.application.dto.recommendation;

import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import java.util.List;

public record RecommendedLocationsResponse(
        List<RecommendedLocationResponse> recommendations
) {

}
