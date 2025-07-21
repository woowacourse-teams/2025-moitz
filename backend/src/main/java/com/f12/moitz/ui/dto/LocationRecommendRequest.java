package com.f12.moitz.ui.dto;

import java.util.List;

public record LocationRecommendRequest(
        List<String> stations,
        String additionalCondition
) {

}
