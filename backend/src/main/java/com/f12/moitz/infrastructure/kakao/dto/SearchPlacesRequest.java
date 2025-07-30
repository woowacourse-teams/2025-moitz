package com.f12.moitz.infrastructure.kakao.dto;

import com.f12.moitz.domain.Point;

public record SearchPlacesRequest(
        String query,
        Point point,
        Integer radius
) {
}
