package com.f12.moitz.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation {

    private List<Candidate> candidates;

    public Recommendation(final List<Candidate> candidates) {
        validate(candidates);
        this.candidates = sort(candidates);
    }

    private void validate(final List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("추천 후보지는 비어있거나 null일 수 없습니다.");
        }
        if (candidates.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 후보지 목록에 null이 포함될 수 없습니다.");
        }
    }

    private List<Candidate> sort(final List<Candidate> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparingInt((Candidate candidate) -> candidate.getTag().getPriority())
                        .thenComparing(Candidate::calculateFairnessScore))
                .toList();
    }

    public int getBestRecommendationTime() {
        return candidates.stream()
                .mapToInt(Candidate::calculateAverageTravelTime)
                .min()
                .orElseThrow();
    }

    public int size() {
        return candidates.size();
    }

    public Candidate get(int index) {
        return candidates.get(index);
    }

}
