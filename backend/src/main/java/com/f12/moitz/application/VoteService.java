package com.f12.moitz.application;

import com.f12.moitz.application.dto.VotesResponse;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.RecommendationVote;
import com.f12.moitz.domain.repository.RecommendResultRepository;
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
    public VotesResponse addVote(final String id, final String locationName) {
        final ObjectId objectId = parseObjectId(id);

        if (!recommendResultRepository.existsById(objectId)) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }

        recommendResultRepository.incrementVotesByIdAndCandidate(objectId, locationName);

        final RecommendationVote result = recommendResultRepository.findVotesByIdAndCandidate(objectId, locationName)
                .orElseThrow(() -> new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT));
        return new VotesResponse(result.getLocation(), result.getVotes());
    }

    public List<VotesResponse> getAllVotes(final String id) {
        final List<RecommendationVote> result = recommendResultRepository.findAllVotesById(parseObjectId(id));
        return result.stream()
                .map(recommendationVote -> new VotesResponse(
                        recommendationVote.getLocation(),
                        recommendationVote.getVotes()
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
