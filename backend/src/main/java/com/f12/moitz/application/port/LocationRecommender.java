package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import java.util.List;
import java.util.Map;

public interface LocationRecommender {

    Map<Place, String> recommendLocations(List<String> startPlaceNames, String condition);

}
