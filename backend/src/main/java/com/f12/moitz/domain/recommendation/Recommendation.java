package com.f12.moitz.domain.recommendation;

import com.f12.moitz.domain.Place;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommendation {

    private List<Candidate> candidates;

    public static Recommendation create(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlaces,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates
    ) {
        final List<Candidate> candidates = recommendedCandidates.getPlaces().stream()
                .map(place -> Candidate.create(
                        place,
                        getRecommendationReason(place, reasonsByPlace),
                        recommendedPlaces.get(place),
                        recommendedCandidateTravels.getCandidateRoutes(place),
                        recommendedCandidates.getTags(place)
                ))
                .toList();
        return new Recommendation(candidates);
    }

    private static RecommendationReason getRecommendationReason(
            final Place place,
            final Map<Place, RecommendationReason> reasonsByPlace
    ) {
        final RecommendationReason recommendationReason = reasonsByPlace.get(place);
        if (recommendationReason == null) {
            throw new IllegalArgumentException("추천 이유가 누락되었습니다. 추천 지역: " + place.getName());
        }
        return recommendationReason;
    }

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
