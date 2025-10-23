package com.f12.moitz.application.port;

import com.f12.moitz.domain.CategorizedRecommendedPlaces;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface AsyncPlaceRecommender {

    Mono<Map<Place, CategorizedRecommendedPlaces>> recommendPlacesAsync(
            List<Place> targetPlaces,
            List<RecommendCondition> requirements
    );

}
