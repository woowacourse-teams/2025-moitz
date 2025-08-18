package com.f12.moitz.infrastructure.client.gemini.dto;

import java.util.List;

public record RecommendedLocationResponse(
        List<LocationNameAndReason> recommendations
) {

}
