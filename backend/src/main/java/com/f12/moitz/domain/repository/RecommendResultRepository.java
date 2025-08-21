package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.Result;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendResultRepository extends MongoRepository<Result, ObjectId> {

    default ObjectId saveAndReturnId(final Result result) {
        Result savedResult = save(result);
        return savedResult.getId();
    }

}
