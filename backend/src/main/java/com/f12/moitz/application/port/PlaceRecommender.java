package com.f12.moitz.application.port;

import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Place;
import java.util.List;
import java.util.Map;

public interface PlaceRecommender {

    Map<Place, CategorizedRecommendedPlaces> recommendPlaces(List<Place> targetPlaces, List<String> requirements);

}
