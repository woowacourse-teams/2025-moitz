package com.f12.moitz.domain.recommendation;

import com.f12.moitz.domain.Place;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;

public class Result {

    private final ObjectId id;

    private final List<RecommendCondition> recommendConditions;

    private final Instant createdAt;

    private final List<? extends Place> startingPlaces;

    private final Recommendation recommendedLocations;

    public Result(
            final List<RecommendCondition> recommendConditions,
            final List<? extends Place> startingPlaces,
            final Recommendation recommendedLocations
    ) {
        this(null, recommendConditions, startingPlaces, recommendedLocations, null);
    }

    public Result(
            final ObjectId id,
            final List<RecommendCondition> recommendConditions,
            final List<? extends Place> startingPlaces,
            final Recommendation recommendedLocations
    ) {
        this(id, recommendConditions, startingPlaces, recommendedLocations, null);
    }

    public Result(
            final ObjectId id,
            final List<RecommendCondition> recommendConditions,
            final List<? extends Place> startingPlaces,
            final Recommendation recommendedLocations,
            final Instant createdAt
    ) {
        validate(recommendConditions, startingPlaces, recommendedLocations);
        this.id = id;
        this.recommendConditions = List.copyOf(recommendConditions);
        this.startingPlaces = List.copyOf(startingPlaces);
        this.recommendedLocations = recommendedLocations;
        this.createdAt = createdAt;
    }

    private void validate(
            final List<RecommendCondition> recommendConditions,
            final List<? extends Place> startingPlaces,
            final Recommendation recommendedLocations
    ) {
        if (recommendConditions == null || recommendConditions.isEmpty()) {
            throw new IllegalArgumentException("추천 조건은 비어있거나 null일 수 없습니다.");
        }
        if (recommendConditions.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("추천 조건에 null이 포함될 수 없습니다.");
        }
        if (startingPlaces == null || startingPlaces.isEmpty()) {
            throw new IllegalArgumentException("출발지들은 비어있거나 null일 수 없습니다.");
        }
        if (startingPlaces.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("출발지 목록에 null이 포함될 수 없습니다.");
        }
        if (recommendedLocations == null) {
            throw new IllegalArgumentException("추천 정보는 null일 수 없습니다.");
        }
    }

    public int getBestRecommendationTime() {
        return recommendedLocations.getBestRecommendationTime();
    }

    public ObjectId getId() {
        return id;
    }

    public List<RecommendCondition> getRecommendConditions() {
        return recommendConditions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<? extends Place> getStartingPlaces() {
        return startingPlaces;
    }

    public Recommendation getRecommendedLocations() {
        return recommendedLocations;
    }

    public int getStartingPlacesCount() {
        return startingPlaces.size();
    }

    public int getRecommendedLocationsCount() {
        return recommendedLocations.size();
    }

}
