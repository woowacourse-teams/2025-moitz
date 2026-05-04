package com.f12.moitz.application;

import com.f12.moitz.application.dto.VotesResponse;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.recommendation.repository.RecommendResultRepository;
import com.f12.moitz.domain.recommendation.repository.dto.CandidateVote;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VoteService {

    private final RecommendResultRepository recommendResultRepository;

    public VoteService(final RecommendResultRepository recommendResultRepository) {
        this.recommendResultRepository = recommendResultRepository;
    }

    @Transactional
    public VotesResponse addVote(final String id, final String candidateName) {
        validateCandidateName(candidateName);
        final ObjectId objectId = parseExistingResultId(id);

        recommendResultRepository.incrementVotesByIdAndCandidate(objectId, candidateName);

        final CandidateVote result = recommendResultRepository.findVotesByIdAndCandidate(objectId, candidateName)
                .orElseThrow(() -> new NotFoundException(GeneralErrorCode.INPUT_INVALID_CANDIDATE_NAME));
        return toResponse(result);
    }

    public List<VotesResponse> getAllVotes(final String id) {
        final List<CandidateVote> result = recommendResultRepository.findAllVotesById(parseExistingResultId(id));
        return result.stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateCandidateName(final String candidateName) {
        if (candidateName == null || candidateName.trim().isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_CANDIDATE_NAME, candidateName);
        }
    }

    private ObjectId parseExistingResultId(final String id) {
        final ObjectId objectId = parseObjectId(id);
        if (!recommendResultRepository.existsById(objectId)) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }
        return objectId;
    }

    private VotesResponse toResponse(final CandidateVote candidateVote) {
        return new VotesResponse(candidateVote.getCandidateName(), candidateVote.getVotes());
    }

    private ObjectId parseObjectId(final String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }
    }

}
