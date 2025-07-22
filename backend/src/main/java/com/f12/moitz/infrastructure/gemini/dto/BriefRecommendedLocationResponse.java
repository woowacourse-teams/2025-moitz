package com.f12.moitz.infrastructure.gemini.dto;

import java.util.List;

public record BriefRecommendedLocationResponse(
        List<LocationNameAndReason> recommendations,
        List<String> additionalConditionsCategoryCodes
) {

}
