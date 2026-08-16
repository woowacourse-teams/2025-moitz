package com.f12.moitz.domain.recommendation.repository;

import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.recommendation.vote.CandidateVote;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

public interface RecommendResultRepository {

    ObjectId saveAndReturnId(Result result);

    Optional<Result> findById(ObjectId id);

    boolean existsById(ObjectId id);

    Optional<CandidateVote> findVotesByIdAndCandidate(ObjectId id, String location);

    List<CandidateVote> findAllVotesById(ObjectId id);

    Optional<CandidateVote> incrementVotesByIdAndCandidate(ObjectId id, String location);

}
