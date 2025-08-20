package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.entity.Result;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendResultRepository extends MongoRepository<Result,String> {

    default String saveAndReturnId(Result result) {
        Result savedResult = save(result);
        return savedResult.getId();
    }

}
