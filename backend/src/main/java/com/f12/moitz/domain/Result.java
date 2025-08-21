package com.f12.moitz.domain;

import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Document(collection = "result")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Result {

    @Id
    private ObjectId id;

    private List<Place> startingPlaces;
    private Recommendation recommendedLocations;

    public Result(
            final List<Place> startingPlaces,
            final Recommendation recommendedLocations
    ) {
        validate(startingPlaces, recommendedLocations);
        this.startingPlaces = startingPlaces;
        this.recommendedLocations = recommendedLocations;
    }

    public Result(
            final ObjectId id,
            final List<Place> startingPlaces,
            final Recommendation recommendedLocations
    ) {
        validate(startingPlaces, recommendedLocations);
        this.id = id;
        this.startingPlaces = startingPlaces;
        this.recommendedLocations = recommendedLocations;
    }

    private void validate(final List<Place> startingPlaces, final Recommendation recommendedLocations) {
        if (startingPlaces == null || startingPlaces.isEmpty()) {
            throw new IllegalArgumentException("출발지들은 비어있거나 null일 수 없습니다.");
        }
        if (recommendedLocations == null) {
            throw new IllegalArgumentException("추천 정보는 null일 수 없습니다.");
        }
    }

    public int getBestRecommendationTime() {
        return recommendedLocations.getBestRecommendationTime();
    }

    public int getStartingPlacesCount() {
        return startingPlaces.size();
    }

    public int getRecommendedLocationsCount() {
        return recommendedLocations.size();
    }

}
