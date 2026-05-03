package com.f12.moitz.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteCandidateScores {

    private final Map<RouteCandidate, FairnessScore> scoresByCandidate;

    public RouteCandidateScores(final List<RouteCandidate> candidates) {
        validate(candidates);
        this.scoresByCandidate = calculateScores(candidates);
    }

    private void validate(final List<RouteCandidate> candidates) {
        if (candidates == null) {
            throw new IllegalArgumentException("후보 목록은 null일 수 없습니다.");
        }
    }

    private Map<RouteCandidate, FairnessScore> calculateScores(final List<RouteCandidate> candidates) {
        return candidates.stream()
                .collect(Collectors.toMap(
                        candidate -> candidate,
                        RouteCandidate::calculateFairnessScore,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    public FairnessScore scoreOf(final RouteCandidate candidate) {
        final FairnessScore score = scoresByCandidate.get(candidate);
        if (score == null) {
            throw new IllegalArgumentException("후보 점수가 존재하지 않습니다.");
        }
        return score;
    }

}
