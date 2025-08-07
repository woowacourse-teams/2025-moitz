package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import java.util.List;
import java.util.Map;

public interface Recommender {

    Place findPlaceByName(String placeName);

    List<Place> findPlacesByNames(List<String> placeNames);

    Map<Place, String> recommendLocations(List<String> startPlaceNames, String condition);

    Map<Place, List<RecommendedPlace>> recommendPlaces(List<Place> targetPlaces, String requirement);

}
