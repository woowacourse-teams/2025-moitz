package com.f12.moitz.infrastructure.persistence.subway.repository;

import com.f12.moitz.infrastructure.persistence.subway.SubwayEdgeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubwayEdgeMongoRepository extends MongoRepository<SubwayEdgeEntity, String> {

}
