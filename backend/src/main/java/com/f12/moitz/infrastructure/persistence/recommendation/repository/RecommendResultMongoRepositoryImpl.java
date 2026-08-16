package com.f12.moitz.infrastructure.persistence.recommendation.repository;

import com.f12.moitz.infrastructure.persistence.recommendation.ResultEntity;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class RecommendResultMongoRepositoryImpl implements RecommendResultMongoRepositoryCustom {

    private static final int MAX_VOTES = 99;

    private final MongoTemplate mongoTemplate;

    public RecommendResultMongoRepositoryImpl(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<ResultEntity> incrementVotesByIdAndCandidate(final ObjectId id, final String location) {
        final Query query = new Query(Criteria.where("_id").is(id)
                .and("recommendedLocations.candidates")
                .elemMatch(Criteria.where("destination.name").is(location).and("votes").lt(MAX_VOTES)));
        final Update update = new Update().inc("recommendedLocations.candidates.$.votes", 1);

        return Optional.ofNullable(mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                ResultEntity.class
        ));
    }

}
