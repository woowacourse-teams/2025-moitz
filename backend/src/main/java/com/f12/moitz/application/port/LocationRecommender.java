package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.application.dto.RecommendedLocationResponse;
import java.util.List;
import java.util.Map;

public interface LocationRecommender {

    RecommendedLocationResponse getRecommendedLocations(final List<String> startPlaceNames, final String condition);

    Map<Place, String> recommendLocations(RecommendedLocationResponse recommendedLocationResponse);

}
