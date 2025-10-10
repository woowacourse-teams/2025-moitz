package com.f12.moitz.application;

import com.f12.moitz.application.dto.VotesResponse;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.RecommendationVote;
import com.f12.moitz.domain.Result;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VoteService {

    private final MongoTemplate mongoTemplate;
    private final RecommendResultRepository recommendResultRepository;

    public VoteService(final MongoTemplate mongoTemplate, final RecommendResultRepository recommendResultRepository) {
        this.mongoTemplate = mongoTemplate;
        this.recommendResultRepository = recommendResultRepository;
    }

    @Transactional
    public VotesResponse addVote(final String id, final String location) {
        if (!recommendResultRepository.existsById(parseObjectId(id))) {
            throw new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT);
        }

        Query query = new Query(Criteria.where("_id").is(id)
                .and("recommendedLocations.candidates.destination.name").is(location));
        Update update = new Update().inc("recommendedLocations.candidates.$.votes", 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Result.class);

        if (updateResult.getModifiedCount() < 1) {
            throw new RuntimeException("UPDATE 작업이 실패했습니다.");
        }

        RecommendationVote result = recommendResultRepository.findVotesByIdAndCandidate(parseObjectId(id), location)
                .orElseThrow(() -> new NotFoundException(GeneralErrorCode.INPUT_INVALID_RESULT));
        return new VotesResponse(result.getLocation(), result.getVotes());
    }

    @Transactional(readOnly = true)
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
