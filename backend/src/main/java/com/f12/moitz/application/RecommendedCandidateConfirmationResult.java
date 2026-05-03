package com.f12.moitz.application;

import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;

public class RecommendedCandidateConfirmationResult {

    private final RecommendedCandidates recommendedCandidates;
    private final RecommendedCandidateTravels recommendedCandidateTravels;

    public RecommendedCandidateConfirmationResult(
            final RecommendedCandidates recommendedCandidates,
            final RecommendedCandidateTravels recommendedCandidateTravels
    ) {
        this.recommendedCandidates = recommendedCandidates;
        this.recommendedCandidateTravels = recommendedCandidateTravels;
    }

    public RecommendedCandidates getRecommendedCandidates() {
        return recommendedCandidates;
    }

    public RecommendedCandidateTravels getRecommendedCandidateTravels() {
        return recommendedCandidateTravels;
    }

}
