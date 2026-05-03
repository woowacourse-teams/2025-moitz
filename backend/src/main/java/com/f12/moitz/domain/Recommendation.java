package com.f12.moitz.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public static Recommendation create(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlacesByPlace,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates,
            final List<RecommendCondition> recommendConditions
    ) {
        return new Recommendation(
                reasonsByPlace,
                recommendedPlacesByPlace,
                recommendedCandidateTravels,
                recommendedCandidates,
                recommendConditions
        );
    }

    private Recommendation(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlacesByPlace,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates,
            final List<RecommendCondition> recommendConditions
    ) {
        validateCreationInputs(
                reasonsByPlace,
                recommendedPlacesByPlace,
                recommendedCandidateTravels,
                recommendedCandidates,
                recommendConditions
        );
        final List<Place> placesReadyForCandidateCreation = findPlacesReadyForCandidateCreation(
                recommendedPlacesByPlace,
                recommendedCandidates,
                recommendConditions
        );
        validateCandidateMaterials(
                placesReadyForCandidateCreation,
                reasonsByPlace,
                recommendedCandidateTravels,
                recommendedCandidates
        );
        final List<Candidate> candidates = placesReadyForCandidateCreation.stream()
                .map(place -> toCandidate(
                        place,
                        reasonsByPlace,
                        recommendedPlacesByPlace,
                        recommendedCandidateTravels,
                        recommendedCandidates
                ))
                .toList();
        validate(candidates);
        this.candidates = sort(candidates);
    }

    private void validateCreationInputs(
            final Map<Place, RecommendationReason> reasonsByPlace,
            final Map<Place, RecommendedPlaces> recommendedPlacesByPlace,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates,
            final List<RecommendCondition> recommendConditions
    ) {
        if (reasonsByPlace == null || reasonsByPlace.isEmpty()) {
            throw new IllegalArgumentException("추천 이유는 비어있거나 null일 수 없습니다.");
        }
        if (recommendedPlacesByPlace == null) {
            throw new IllegalArgumentException("추천 장소 목록은 null일 수 없습니다.");
        }
        if (recommendedCandidateTravels == null) {
            throw new IllegalArgumentException("추천 후보 이동 정보는 null일 수 없습니다.");
        }
        if (recommendedCandidates == null) {
            throw new IllegalArgumentException("추천 후보는 null일 수 없습니다.");
        }
        if (recommendConditions == null || recommendConditions.isEmpty()) {
            throw new IllegalArgumentException("추천 조건은 비어있거나 null일 수 없습니다.");
        }
    }

    private List<Place> findPlacesReadyForCandidateCreation(
            final Map<Place, RecommendedPlaces> recommendedPlacesByPlace,
            final RecommendedCandidates recommendedCandidates,
            final List<RecommendCondition> recommendConditions
    ) {
        return recommendedCandidates.getPlaces().stream()
                .filter(place -> hasRequiredRecommendedPlaces(
                        recommendedPlacesByPlace.get(place),
                        recommendConditions
                ))
                .toList();
    }

    private void validateCandidateMaterials(
            final List<Place> placesReadyForCandidateCreation,
            final Map<Place, RecommendationReason> reasonsByPlace,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates
    ) {
        placesReadyForCandidateCreation.forEach(place -> validateCandidateMaterials(
                place,
                reasonsByPlace,
                recommendedCandidateTravels,
                recommendedCandidates
        ));
    }

    private void validateCandidateMaterials(
            final Place place,
            final Map<Place, RecommendationReason> reasonsByPlace,
            final RecommendedCandidateTravels recommendedCandidateTravels,
            final RecommendedCandidates recommendedCandidates
    ) {
        getRecommendationReason(place, reasonsByPlace);
        recommendedCandidateTravels.getCandidateRoutes(place);
        recommendedCandidates.getTags(place);
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

    private Candidate toCandidate(
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
