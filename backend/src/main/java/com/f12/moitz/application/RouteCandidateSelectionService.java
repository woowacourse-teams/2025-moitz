package com.f12.moitz.application;

import com.f12.moitz.domain.CandidateSelection;
import com.f12.moitz.domain.CandidateSelectionPolicy;
import com.f12.moitz.domain.DispersionPolicy;
import org.springframework.stereotype.Service;

@Service
public class RouteCandidateSelectionService {

    private final CandidateSelectionPolicy candidateSelectionPolicy = new CandidateSelectionPolicy();

    public CandidateSelection select(
            final RouteCandidatePreparationResult routeCandidatePreparationResult,
            final DispersionPolicy dispersionPolicy,
            final int limit
    ) {
        return candidateSelectionPolicy.select(
                routeCandidatePreparationResult.getRouteCandidates(),
                dispersionPolicy,
                limit
        );
    }

}
