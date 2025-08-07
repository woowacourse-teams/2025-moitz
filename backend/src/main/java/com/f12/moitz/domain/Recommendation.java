package com.f12.moitz.domain;

import java.util.Comparator;
import java.util.List;
import lombok.Getter;

@Getter
public class Recommendation {

    private final List<Candidate> candidates;

    public Recommendation(final List<Candidate> candidates) {
        validate(candidates);
        this.candidates = sort(candidates);
    }

    private void validate(final List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("추천 후보지는 비어있거나 null일 수 없습니다.");
        }
    }

    private List<Candidate> sort(final List<Candidate> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparingInt(Candidate::calculateAverageTravelTime))
                .toList();
    }

    public int getBestRecommendationTime() {
        return candidates.getFirst().calculateAverageTravelTime();
    }

    public int size() {
        return candidates.size();
    }

    public Candidate get(int index) {
        return candidates.get(index);
    }

}
