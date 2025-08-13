package com.f12.moitz.infrastructure.kakao.dto;

public record SearchPlacesRequest(
        String query,
        Double longitude,
        Double latitude,
        Integer radius
) {
}
