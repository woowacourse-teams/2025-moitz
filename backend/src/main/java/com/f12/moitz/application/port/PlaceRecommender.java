package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import java.util.List;
import java.util.Map;

public interface PlaceRecommender {

    Map<Place, List<RecommendedPlace>> recommendPlaces(List<Place> targetPlaces, String requirement);

}
