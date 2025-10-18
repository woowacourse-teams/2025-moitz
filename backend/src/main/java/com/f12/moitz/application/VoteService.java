package com.f12.moitz.application;

import com.f12.moitz.application.dto.VotesResponse;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.repository.dto.CandidateVote;
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
        final ObjectId objectId = parseObjectId(id);

        if (!recommendResultRepository.existsById(objectId)) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }

        recommendResultRepository.incrementVotesByIdAndCandidate(objectId, candidateName);

        final CandidateVote result = recommendResultRepository.findVotesByIdAndCandidate(objectId, candidateName)
                .orElseThrow(() -> new NotFoundException(GeneralErrorCode.INPUT_INVALID_CANDIDATE_NAME));
        return new VotesResponse(result.getCandidateName(), result.getVotes());
    }

    public List<VotesResponse> getAllVotes(final String id) {
        final List<CandidateVote> result = recommendResultRepository.findAllVotesById(parseObjectId(id));
        return result.stream()
                .map(candidateVote -> new VotesResponse(
                        candidateVote.getCandidateName(),
                        candidateVote.getVotes()
                ))
                .toList();
    }

    private ObjectId parseObjectId(final String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }
    }

}
