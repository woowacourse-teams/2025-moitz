package com.f12.moitz.infrastructure.client.kakao.dto;

public record SearchPlacesLimitQuantityRequest(
        String query,
        Double longitude,
        Double latitude,
        Integer radius,
        Integer size
) {

}
