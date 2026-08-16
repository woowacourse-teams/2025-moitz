package com.f12.moitz.infrastructure.persistence.recommendation.repository;

import com.f12.moitz.infrastructure.persistence.recommendation.ResultEntity;
import java.util.Optional;
import org.bson.types.ObjectId;

public interface RecommendResultMongoRepositoryCustom {

    Optional<ResultEntity> incrementVotesByIdAndCandidate(ObjectId id, String location);

}
