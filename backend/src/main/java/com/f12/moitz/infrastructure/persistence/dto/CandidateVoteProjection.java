package com.f12.moitz.infrastructure.persistence.dto;

import com.f12.moitz.domain.recommendation.repository.dto.CandidateVote;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class CandidateVoteProjection {

    @Field("recommendedLocations.candidates.destination.name")
    private String candidateName;

    @Field("recommendedLocations.candidates.votes")
    private Integer votes;

    public CandidateVote toCandidateVote() {
        return new CandidateVote(candidateName, votes);
    }

}
