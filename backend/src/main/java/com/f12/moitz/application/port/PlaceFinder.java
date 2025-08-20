package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import java.util.List;

public interface PlaceFinder {

    Place findPlaceByName(String placeName);

    List<Place> findPlacesByNames(List<String> placeNames);

}
