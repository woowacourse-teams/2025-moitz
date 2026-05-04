package com.f12.moitz.infrastructure.persistence;

import com.f12.moitz.domain.recommendation.Result;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.repository.dto.CandidateVote;
import com.f12.moitz.infrastructure.persistence.dto.CandidateVoteProjection;
import com.f12.moitz.infrastructure.persistence.repository.RecommendResultMongoRepository;
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
        return recommendResultMongoRepository.save(result).getId();
    }

    @Override
    public Optional<Result> findById(final ObjectId id) {
        return recommendResultMongoRepository.findById(id);
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
