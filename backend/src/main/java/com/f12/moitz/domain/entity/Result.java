package com.f12.moitz.domain.entity;

import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@Document(collection = "result")
public class Result {

    @Transient
    public static final String SEQUENCE_NAME = "result";

    @Id
    private String id;
    private List<StartingPlaceResponse> startingPlaces;
    private List<RecommendationResponse> recommendedLocations;

    protected Result() {}

    public Result(
            final List<StartingPlaceResponse> startingPlaces,
            final List<RecommendationResponse> recommendedLocations
    ) {
        this.startingPlaces = startingPlaces;
        this.recommendedLocations = recommendedLocations;
    }




}
