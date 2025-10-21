package com.f12.moitz.infrastructure.client.kakao.dto;

public record SearchPlacesLimitQuantityRequest(
        String query,
        String name,
        Double longitude,
        Double latitude,
        Integer radius,
        Integer size
) {

}
