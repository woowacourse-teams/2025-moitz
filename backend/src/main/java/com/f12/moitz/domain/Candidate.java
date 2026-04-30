package com.f12.moitz.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candidate {

    private Place destination;
    private Routes routes;
    private Courses courses;
    private CategorizedRecommendedPlaces recommendedPlaces;
    private List<CandidateSelectionTag> tags;
    private String description;
    private String reason;
    private int votes;

    public Candidate(
            final Place destination,
            final Routes routes,
            final Courses courses,
            final CategorizedRecommendedPlaces recommendedPlaces,
            final CandidateSelectionTag tag,
            final String description,
            final String reason,
            final int votes
    ) {
        this(destination, routes, courses, recommendedPlaces, List.of(resolveNullableTag(tag)), description, reason, votes);
    }

    public Candidate(
            final Place destination,
            final Routes routes,
            final Courses courses,
            final CategorizedRecommendedPlaces recommendedPlaces,
            final List<CandidateSelectionTag> tags,
            final String description,
            final String reason,
            final int votes
    ) {
        validate(destination, routes, courses, recommendedPlaces, description, reason, votes);
        this.destination = destination;
        this.routes = routes;
        this.courses = courses;
        this.recommendedPlaces = recommendedPlaces;
        this.tags = resolveTags(tags);
        this.description = description;
        this.reason = reason;
        this.votes = votes;
    }

    private void validate(
            final Place suggestedLocation,
            final Routes routes,
            final Courses courses,
            final CategorizedRecommendedPlaces recommendedPlaces,
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

    private static CandidateSelectionTag resolveNullableTag(final CandidateSelectionTag tag) {
        if (tag == null) {
            return CandidateSelectionTag.GENERAL;
        }
        return tag;
    }

    private List<CandidateSelectionTag> resolveTags(final List<CandidateSelectionTag> tags) {
        return CandidateSelectionTag.normalize(tags);
    }

    public int calculateAverageTravelTime() {
        return routes.calculateAverageTravelTime();
    }

    public CandidateSelectionTag getTag() {
        return getTags().getFirst();
    }

    public List<CandidateSelectionTag> getTags() {
        return resolveTags(tags);
    }

}
