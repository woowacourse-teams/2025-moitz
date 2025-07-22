package com.f12.moitz.application.dto;

public record PlaceResponse(
        double latitude,
        double longitude,
        String placeName
) {

}
