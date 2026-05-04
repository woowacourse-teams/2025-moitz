package com.f12.moitz.infrastructure.persistence.recommendation;

import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.recommendation.repository.RecommendResultRepository;
import com.f12.moitz.domain.recommendation.vote.CandidateVote;
import com.f12.moitz.infrastructure.persistence.recommendation.projection.CandidateVoteProjection;
import com.f12.moitz.infrastructure.persistence.recommendation.repository.RecommendResultMongoRepository;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class RecommendResultRepositoryAdapter implements RecommendResultRepository {

    private final RecommendResultMongoRepository recommendResultMongoRepository;

    public RecommendResultRepositoryAdapter(final RecommendResultMongoRepository recommendResultMongoRepository) {
        this.recommendResultMongoRepository = recommendResultMongoRepository;
    }

    @Override
    public ObjectId saveAndReturnId(final Result result) {
        return recommendResultMongoRepository.save(ResultEntity.fromDomain(result)).getId();
    }

    @Override
    public Optional<Result> findById(final ObjectId id) {
        return recommendResultMongoRepository.findById(id)
                .map(ResultEntity::toDomain);
    }

    @Override
    public boolean existsById(final ObjectId id) {
        return recommendResultMongoRepository.existsById(id);
    }

    @Override
    public Optional<CandidateVote> findVotesByIdAndCandidate(final ObjectId id, final String location) {
        return recommendResultMongoRepository.findVotesByIdAndCandidate(id, location)
                .map(CandidateVoteProjection::toCandidateVote);
    }

    @Override
    public List<CandidateVote> findAllVotesById(final ObjectId id) {
        return recommendResultMongoRepository.findAllVotesById(id).stream()
                .map(CandidateVoteProjection::toCandidateVote)
                .toList();
    }

    @Override
    public void incrementVotesByIdAndCandidate(final ObjectId id, final String location) {
        recommendResultMongoRepository.incrementVotesByIdAndCandidate(id, location);
    }

}
