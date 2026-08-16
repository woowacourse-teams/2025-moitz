package com.f12.moitz.infrastructure.persistence.recommendation.repository;

import com.f12.moitz.infrastructure.persistence.recommendation.ResultEntity;
import com.f12.moitz.infrastructure.persistence.recommendation.projection.CandidateVoteProjection;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendResultMongoRepository extends MongoRepository<ResultEntity, ObjectId>,
        RecommendResultMongoRepositoryCustom {

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $unwind: '$recommendedLocations.candidates' }",
            "{ $match: { 'recommendedLocations.candidates.destination.name': ?1 } }",
            "{ $project: { " +
                    "'recommendedLocations.candidates.destination.name': 1, " +
                    "'recommendedLocations.candidates.votes': 1, " +
                    "'_id': 0 } }"
    })
    Optional<CandidateVoteProjection> findVotesByIdAndCandidate(ObjectId id, String location);

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $unwind: '$recommendedLocations.candidates' }",
            "{ $project: { " +
                    "'recommendedLocations.candidates.destination.name': 1, " +
                    "'recommendedLocations.candidates.votes': 1, " +
                    "'_id': 0 } }"
    })
    List<CandidateVoteProjection> findAllVotesById(ObjectId id);

}
