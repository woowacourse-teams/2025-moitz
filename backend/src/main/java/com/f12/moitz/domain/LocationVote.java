package com.f12.moitz.domain;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class LocationVote {

    private ObjectId id;

    @Field("recommendedLocations.candidates.destination.name")
    private String location;

    @Field("recommendedLocations.candidates.votes")
    private Integer votes;

}
