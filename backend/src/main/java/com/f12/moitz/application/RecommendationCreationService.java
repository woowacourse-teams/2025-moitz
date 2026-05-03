package com.f12.moitz.application;

import com.f12.moitz.domain.Candidate;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.Recommendation;
import com.f12.moitz.domain.RecommendationReason;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RecommendedPlaces;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RecommendationCreationService {

    public Recommendation create(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlaces,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates,
            final List<RecommendCondition> recommendConditions
    ) {
        final List<Candidate> candidates = recommendedCandidates.getPlaces().stream()
                .filter(place -> hasRequiredRecommendedPlaces(recommendedPlaces.get(place), recommendConditions))
                .map(place -> createCandidate(
                        place,
                        reasonsByPlace,
                        recommendedPlaces,
                        recommendedCandidateTravels,
                        recommendedCandidates
                ))
                .toList();
        return new Recommendation(candidates);
    }

    private boolean hasRequiredRecommendedPlaces(
            final RecommendedPlaces recommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        if (recommendedPlaces == null) {
            return false;
        }
        return recommendedPlaces.satisfiesAllConditions(recommendConditions);
    }

    private Candidate createCandidate(
            final Place place,
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlaces,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates
    ) {
        return Candidate.create(
                place,
                getRecommendationReason(place, reasonsByPlace),
                recommendedPlaces.get(place),
                recommendedCandidateTravels.getCandidateRoutes(place),
                recommendedCandidates.getTags(place)
        );
    }

    private RecommendationReason getRecommendationReason(
            final Place place,
            final Map<Place, RecommendationReason> reasonsByPlace
    ) {
        final RecommendationReason recommendationReason = reasonsByPlace.get(place);
        if (recommendationReason == null) {
            throw new IllegalArgumentException("추천 이유가 누락되었습니다. 추천 지역: " + place.getName());
        }
        return recommendationReason;
    }

}
