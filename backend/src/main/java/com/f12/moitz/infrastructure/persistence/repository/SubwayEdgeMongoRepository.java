package com.f12.moitz.infrastructure.persistence.repository;

import com.f12.moitz.infrastructure.persistence.SubwayEdgeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubwayEdgeMongoRepository extends MongoRepository<SubwayEdgeEntity, String> {

}
