package com.f12.moitz.application.dto;

public record LocationRecommendResponse(
        int index,
        double y,
        double x,
        String placeName,
        int averageTime,
        boolean isBest,
        String aiDescription
) {

}
