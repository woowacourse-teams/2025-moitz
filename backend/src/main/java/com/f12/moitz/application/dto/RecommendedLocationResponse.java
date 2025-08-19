package com.f12.moitz.application.dto;

import com.f12.moitz.infrastructure.client.gemini.dto.LocationNameAndReason;
import java.util.List;

public record RecommendedLocationResponse(
        List<LocationNameAndReason> recommendations
) {

}
