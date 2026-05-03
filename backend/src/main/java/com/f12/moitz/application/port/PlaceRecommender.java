package com.f12.moitz.application.port;

import com.f12.moitz.domain.RecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import java.util.List;
import java.util.Map;

public interface PlaceRecommender {

    Map<Place, RecommendedPlaces> recommendPlaces(List<Place> targetPlaces, List<RecommendCondition> requirements);

}
