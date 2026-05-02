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
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlacesByPlace,
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace,
            final int votes,
            final List<RecommendCondition> recommendConditions
    ) {
        validateCreationInputs(
                reasonsByPlace,
                recommendedPlacesByPlace,
                routesByPlace,
                coursesByPlace,
                tagsByPlace,
                recommendConditions
        );
        return new Recommendation(
                reasonsByPlace.entrySet().stream()
                        .filter(entry -> hasRequiredRecommendedPlaces(
                                recommendedPlacesByPlace.get(entry.getKey()),
                                recommendConditions
                        ))
                        .map(entry -> toCandidate(
                                entry,
                                recommendedPlacesByPlace,
                                routesByPlace,
                                coursesByPlace,
                                tagsByPlace,
                                votes
                        ))
                        .toList()
        );
    }

    private static void validateCreationInputs(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, CategorizedRecommendedPlaces> recommendedPlacesByPlace,
            final Map<Place, Routes> routesByPlace,
            final Map<Place, Courses> coursesByPlace,
            final Map<Place, List<CandidateSelectionTag>> tagsByPlace,
            final List<RecommendCondition> recommendConditions
    ) {
        if (reasonsByPlace == null || reasonsByPlace.isEmpty()) {
            throw new IllegalArgumentException("추천 이유는 비어있거나 null일 수 없습니다.");
        }
        if (recommendedPlacesByPlace == null) {
            throw new IllegalArgumentException("추천 장소 목록은 null일 수 없습니다.");
        }
        if (routesByPlace == null) {
            throw new IllegalArgumentException("추천 후보 경로는 null일 수 없습니다.");
        }
        if (coursesByPlace == null) {
            throw new IllegalArgumentException("추천 후보 이동 코스는 null일 수 없습니다.");
        }
        if (tagsByPlace == null) {
            throw new IllegalArgumentException("추천 후보 태그는 null일 수 없습니다.");
        }
        if (recommendConditions == null || recommendConditions.isEmpty()) {
            throw new IllegalArgumentException("추천 조건은 비어있거나 null일 수 없습니다.");
        }
    }

    private static boolean hasRequiredRecommendedPlaces(
            final CategorizedRecommendedPlaces recommendedPlaces,
            final List<RecommendCondition> recommendConditions
    ) {
        if (recommendedPlaces == null) {
            return false;
        }
        return recommendedPlaces.satisfiesAll(recommendConditions);
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
        return Candidate.create(
                place,
                reason,
                recommendedPlaces.get(place),
                routesByPlace.get(place),
                coursesByPlace.get(place),
                tagsByPlace.getOrDefault(place, List.of(CandidateSelectionTag.GENERAL)),
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
