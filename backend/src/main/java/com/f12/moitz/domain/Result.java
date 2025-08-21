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

    private List<StartingPlaceResponse> startingPlaces;
    private List<RecommendationResponse> recommendedLocations;

    public Result(
            final List<StartingPlaceResponse> startingPlaces,
            final List<RecommendationResponse> recommendedLocations
    ) {
        this.startingPlaces = startingPlaces;
        this.recommendedLocations = recommendedLocations;
    }

}
