package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PlaceRecommender {

    Place findPlaceByName(String placeName);

    List<Place> findPlacesByNames(List<String> placeNames);

    Map<Place, List<RecommendedPlace>> recommendPlaces(Set<Place> targetPlaces, String requirement);

}
