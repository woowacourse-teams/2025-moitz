package com.f12.moitz.application;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.RecommendCondition;
import com.f12.moitz.domain.RecommendedCandidateTravels;
import com.f12.moitz.domain.RecommendedCandidates;
import com.f12.moitz.domain.RouteOrigins;
import com.f12.moitz.domain.Routes;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RecommendedCandidateConfirmationService {

    private final RecommendedCandidateRouteService recommendedCandidateRouteService;
    private final RecommendationFlowLogger recommendationFlowLogger = new RecommendationFlowLogger();

    public RecommendedCandidateConfirmationService(
            final RecommendedCandidateRouteService recommendedCandidateRouteService
    ) {
        this.recommendedCandidateRouteService = recommendedCandidateRouteService;
    }

    public RecommendedCandidateConfirmationResult confirm(
            final List<Place> searchCandidatePlaces,
            final RecommendationPlaceSearchResult recommendationPlaceSearchResult,
            final RouteOrigins routeOrigins,
            final Map<Place, Routes> candidateRoutes,
            final List<RecommendCondition> recommendConditions
    ) {
        final RecommendedCandidates recommendedCandidates = recommendationPlaceSearchResult.getRecommendedCandidates();
        recommendationFlowLogger.logRecommendedCandidates(
                searchCandidatePlaces,
                recommendedCandidates,
                candidateRoutes,
                recommendConditions
        );
        validateRecommendationCandidates(recommendedCandidates);
        final RecommendedCandidateTravels recommendedCandidateTravels = recommendedCandidateRouteService.prepare(
                routeOrigins,
                recommendedCandidates,
                candidateRoutes
        );
        return new RecommendedCandidateConfirmationResult(recommendedCandidates, recommendedCandidateTravels);
    }

    private void validateRecommendationCandidates(final RecommendedCandidates recommendedCandidates) {
        if (recommendedCandidates.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.RECOMMENDATION_NOT_FOUND);
        }
    }

}
