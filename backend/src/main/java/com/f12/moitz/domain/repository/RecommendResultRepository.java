package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.repository.dto.CandidateVote;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface RecommendResultRepository extends MongoRepository<Result, ObjectId> {

    default ObjectId saveAndReturnId(final Result result) {
        Result savedResult = save(result);
        return savedResult.getId();
    }

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $unwind: '$recommendedLocations.candidates' }",
            "{ $match: { 'recommendedLocations.candidates.destination.name': ?1 } }",
            "{ $project: { " +
                    "'recommendedLocations.candidates.destination.name': 1, " +
                    "'recommendedLocations.candidates.votes': 1, " +
                    "'_id': 0 } }"
    })
    Optional<CandidateVote> findVotesByIdAndCandidate(ObjectId id, String location);

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $unwind: '$recommendedLocations.candidates' }",
            "{ $project: { " +
                    "'recommendedLocations.candidates.destination.name': 1, " +
                    "'recommendedLocations.candidates.votes': 1, " +
                    "'_id': 0 } }"
    })
    List<CandidateVote> findAllVotesById(ObjectId id);

    @Query("{ '_id': ?0, 'recommendedLocations.candidates.destination.name': ?1 }")
    @Update(update = "{ '$inc': { 'recommendedLocations.candidates.$.votes': 1 } }")
    void incrementVotesByIdAndCandidate(ObjectId id, String location);

}
