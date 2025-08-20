package com.f12.moitz.application.dto;

import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import java.util.List;

public record RecommendedLocationsResponse(
        List<RecommendedLocationResponse> recommendations
) {

}
