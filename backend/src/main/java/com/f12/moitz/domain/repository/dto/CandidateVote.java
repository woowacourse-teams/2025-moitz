package com.f12.moitz.domain.repository.dto;

import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class CandidateVote {

    private ObjectId id;

    @Field("recommendedLocations.candidates.destination.name")
    private String candidateName;

    @Field("recommendedLocations.candidates.votes")
    private Integer votes;

    public CandidateVote(final String candidateName, final Integer votes) {
        validate(candidateName, votes);
        this.candidateName = candidateName;
        this.votes = votes;
    }

    private void validate(final String location, final Integer votes) {
        if (location == null) {
            throw new IllegalArgumentException("추천 지역은 필수입니다.");
        }
        if (votes == null) {
            throw new IllegalArgumentException("투표 개수는 필수입니다.");
        }
        if (votes < 0) {
            throw new IllegalArgumentException("투표 개수는 음수일 수 없습니다.");
        }
    }

}
