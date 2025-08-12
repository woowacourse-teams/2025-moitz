package com.f12.moitz.application.port;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendedPlace;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncPlaceRecommender extends PlaceRecommender {

    CompletableFuture<Map<Place, List<RecommendedPlace>>> recommendPlacesAsync (List<Place> targets, String requirement);

}
