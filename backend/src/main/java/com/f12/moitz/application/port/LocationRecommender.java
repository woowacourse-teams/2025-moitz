package com.f12.moitz.application.port;

import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.domain.Place;
import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import java.util.List;
import java.util.Map;

public interface LocationRecommender {

    RecommendedLocationsResponse getRecommendedLocations(final List<String> startPlaceNames, final String condition);

    Map<Place, ReasonAndDescription> recommendLocations(RecommendedLocationsResponse recommendedLocationsResponse);

}
