package com.f12.moitz.application.port.place;

import com.f12.moitz.domain.recommendation.RecommendedPlaces;
import com.f12.moitz.domain.place.Place;
import java.util.List;
import java.util.Map;

public interface PlaceRecommender {

    Map<Place, RecommendedPlaces> recommendPlaces(
            List<Place> targetPlaces,
            PlaceRecommendationCriteria criteria
    );

}
