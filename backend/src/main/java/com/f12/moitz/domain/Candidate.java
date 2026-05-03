package com.f12.moitz.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candidate {

    private static final int INITIAL_VOTES = 0;

    private Place destination;
    private Routes routes;
    private Courses courses;
    private RecommendedPlaces recommendedPlaces;
    private List<CandidateSelectionTag> tags;
    private String description;
    private String reason;
    private int votes;

    public static Candidate create(
            final Place destination,
            final RecommendationReason recommendationReason,
            final RecommendedPlaces recommendedPlaces,
            final Routes routes,
            final Courses courses,
            final List<CandidateSelectionTag> tags
    ) {
        return new Candidate(
                destination,
                recommendationReason,
                recommendedPlaces,
                routes,
                courses,
                tags,
                INITIAL_VOTES
        );
    }

    private Candidate(
            final Place destination,
            final RecommendationReason recommendationReason,
            final RecommendedPlaces recommendedPlaces,
            final Routes routes,
            final Courses courses,
            final List<CandidateSelectionTag> tags,
            final int votes
    ) {
        validateRecommendationReason(recommendationReason);
        validate(
                destination,
                routes,
                courses,
                recommendedPlaces,
                recommendationReason.description(),
                recommendationReason.reason(),
                votes
        );
        assignFields(
                destination,
                routes,
                courses,
                recommendedPlaces,
                resolveTags(tags),
                recommendationReason.description(),
                recommendationReason.reason(),
                votes
        );
    }

    public Candidate(
            final Place destination,
            final Routes routes,
            final Courses courses,
            final RecommendedPlaces recommendedPlaces,
            final CandidateSelectionTag tag,
            final String description,
            final String reason,
            final int votes
    ) {
        validate(destination, routes, courses, recommendedPlaces, description, reason, votes);
        assignFields(
                destination,
                routes,
                courses,
                recommendedPlaces,
                resolveTag(tag),
                description,
                reason,
                votes
        );
    }

    public Candidate(
            final Place destination,
            final Routes routes,
            final Courses courses,
            final RecommendedPlaces recommendedPlaces,
            final List<CandidateSelectionTag> tags,
            final String description,
            final String reason,
            final int votes
    ) {
        validate(destination, routes, courses, recommendedPlaces, description, reason, votes);
        assignFields(
                destination,
                routes,
                courses,
                recommendedPlaces,
                resolveTags(tags),
                description,
                reason,
                votes
        );
    }

    private void assignFields(
            final Place destination,
            final Routes routes,
            final Courses courses,
            final RecommendedPlaces recommendedPlaces,
            final List<CandidateSelectionTag> tags,
            final String description,
            final String reason,
            final int votes
    ) {
        this.destination = destination;
        this.routes = routes;
        this.courses = courses;
        this.recommendedPlaces = recommendedPlaces;
        this.tags = tags;
        this.description = description;
        this.reason = reason;
        this.votes = votes;
    }

    private void validate(
            final Place suggestedLocation,
            final Routes routes,
            final Courses courses,
            final RecommendedPlaces recommendedPlaces,
            final String description,
            final String reason,
            final int votes
    ) {
        if (suggestedLocation == null) {
            throw new IllegalArgumentException("추천 지역은 필수입니다.");
        }
        if (routes == null) {
            throw new IllegalArgumentException("경로 목록은 필수입니다.");
        }
        if (courses == null) {
            throw new IllegalArgumentException("이동 코스 목록은 필수입니다.");
        }
        if (routes.size() != courses.size()) {
            throw new IllegalArgumentException("경로 목록과 이동 코스 목록의 개수는 같아야 합니다.");
        }
        if (recommendedPlaces == null || recommendedPlaces.isEmpty()) {
            throw new IllegalArgumentException("추천 장소 목록은 비어 있을 수 없습니다.");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("추천 설명은 비어 있을 수 없습니다.");
        }
        if (reason == null || reason.isEmpty()) {
            throw new IllegalArgumentException("추천 이유는 비어 있을 수 없습니다.");
        }
        if (votes < 0) {
            throw new IllegalArgumentException("투표 개수는 음수일 수 없습니다.");
        }
    }

    private void validateRecommendationReason(final RecommendationReason recommendationReason) {
        if (recommendationReason == null) {
            throw new IllegalArgumentException("추천 이유는 필수입니다.");
        }
    }

    private List<CandidateSelectionTag> resolveTag(final CandidateSelectionTag tag) {
        if (tag == null) {
            return CandidateSelectionTag.normalize(null);
        }
        return CandidateSelectionTag.normalize(List.of(tag));
    }

    private List<CandidateSelectionTag> resolveTags(final List<CandidateSelectionTag> tags) {
        return CandidateSelectionTag.normalize(tags);
    }

    public int calculateAverageTravelTime() {
        return routes.calculateAverageTravelTime();
    }

    public FairnessScore calculateFairnessScore() {
        return routes.calculateFairnessScore();
    }

    public CandidateSelectionTag getTag() {
        return getTags().getFirst();
    }

    public List<CandidateSelectionTag> getTags() {
        return resolveTags(tags);
    }

    public int getRouteCount() {
        return routes.size();
    }

    public Route getRoute(final int index) {
        return routes.get(index);
    }

    public Course getCourse(final int index) {
        return courses.get(index);
    }

}
