package com.f12.moitz.infrastructure.persistence;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.Recommendation;
import com.f12.moitz.domain.recommendation.Result;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultEntity {

    @Id
    private ObjectId id;

    private List<RecommendCondition> recommendConditions;

    @CreatedDate
    private Instant createdAt;

    private List<? extends Place> startingPlaces;

    private Recommendation recommendedLocations;

    private ResultEntity(
            final ObjectId id,
            final List<RecommendCondition> recommendConditions,
            final Instant createdAt,
            final List<? extends Place> startingPlaces,
            final Recommendation recommendedLocations
    ) {
        this.id = id;
        this.recommendConditions = recommendConditions;
        this.createdAt = createdAt;
        this.startingPlaces = startingPlaces;
        this.recommendedLocations = recommendedLocations;
    }

    public Result toDomain() {
        return new Result(
                id,
                recommendConditions,
                startingPlaces,
                recommendedLocations,
                createdAt
        );
    }

    public static ResultEntity fromDomain(final Result result) {
        return new ResultEntity(
                result.getId(),
                result.getRecommendConditions(),
                result.getCreatedAt(),
                result.getStartingPlaces(),
                result.getRecommendedLocations()
        );
    }

}
