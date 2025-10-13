package com.f12.moitz.application.port;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import java.util.List;

public interface LocationRecommender {

    RecommendedLocationsResponse recommendLocations(
            List<String> startingPlaces,
            List<String> candidatePlaces,
            List<String> requirements
    );
}
