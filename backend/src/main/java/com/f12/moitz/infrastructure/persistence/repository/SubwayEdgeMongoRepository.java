package com.f12.moitz.infrastructure.persistence.repository;

import com.f12.moitz.infrastructure.persistence.SubwayEdgeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubwayEdgeMongoRepository extends MongoRepository<SubwayEdgeDocument, String> {

}
