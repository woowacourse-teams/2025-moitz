package com.f12.moitz.application.port.place;

import com.f12.moitz.domain.place.Place;
import java.util.List;

public interface PlaceFinder {

    Place findPlaceByName(String placeName);

    List<Place> findPlacesByNames(List<String> placeNames);

}
