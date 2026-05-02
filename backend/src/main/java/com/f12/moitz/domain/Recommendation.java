package com.f12.moitz.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class Recommendation {

    private final List<Candidate> candidates;

    public Recommendation(final List<Candidate> candidates) {
        validate(candidates);
        this.candidates = sort(candidates);
    }

    public static Recommendation create(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace,
            final int votes,
            final List<RecommendCondition> recommendConditions
    ) {
        return new Recommendation(
                reasonsByPlace.entrySet().stream()
                        .filter(entry -> hasRequiredRecommendedPlaces(
                                recommendedPlaces.get(entry.getKey()),
                                recommendConditions
                        ))
                        .map(entry -> toCandidate(
                                entry,
                                recommendedPlaces,
                                routesByPlace,
                                coursesByPlace,
                                tagsByPlace,
                                votes
                        ))
                        .toList()
        );
    }

    private static boolean hasRequiredRecommendedPlaces(
            final CategorizedRecommendedPlaces recommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        if (recommendedPlaces == null) {
            return false;
        }
        final Map<RecommendCondition, List<RecommendedPlace>> categoryMap = recommendedPlaces.getCategorizedPlaces();
        return recommendConditions.stream()
                .allMatch(condition -> categoryMap.containsKey(condition) && !categoryMap.get(condition).isEmpty());
    }

    private static Candidate toCandidate(
            final Map.Entry<Place, RecommendationReason> entry,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlaces,
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace,
            final int votes
    ) {
        final Place place = entry.getKey();
        final RecommendationReason reason = entry.getValue();
        return new Candidate(
                place,
                routesByPlace.get(place),
                coursesByPlace.get(place),
                recommendedPlaces.get(place),
                tagsByPlace.getOrDefault(place, List.of(CandidateSelectionTag.GENERAL)),
                reason.description(),
                reason.reason(),
                votes
        );
    }

    private void validate(final List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("추천 후보지는 비어있거나 null일 수 없습니다.");
        }
    }

    private List<Candidate> sort(final List<Candidate> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparingInt((Candidate candidate) -> candidate.getTag().getPriority())
                        .thenComparing(candidate -> candidate.getRoutes().calculateFairnessScore()))
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
