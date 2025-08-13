package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import com.f12.moitz.infrastructure.gemini.dto.RecommendedLocationResponse;

import java.util.List;
import java.util.Map;

public interface Recommender {

    Place findPlaceByName(String placeName);

    List<Place> findPlacesByNames(List<String> placeNames);

    RecommendedLocationResponse getRecommendedLocations(final List<String> startPlaceNames, final String condition);

    Map<Place, String> recommendLocations(RecommendedLocationResponse recommendedLocationResponse);

    Map<Place, List<RecommendedPlace>> recommendPlaces(List<Place> targetPlaces, String requirement);

}
