package com.f12.moitz.application.recommendation;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.recommendation.RecommendationReason;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RecommendedCandidateReasonService {

    public Map<Place, RecommendationReason> generate(final RecommendedCandidates recommendedCandidates) {
        return recommendedCandidates.getPlaces().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        place -> RecommendationReason.fromSelectionTags(
                                place.getName(),
                                recommendedCandidates.getTags(place)
                        ),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

}
