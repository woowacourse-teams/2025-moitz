package com.f12.moitz.infrastructure.persistence.subway.repository;

import com.f12.moitz.infrastructure.persistence.subway.SubwayStationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubwayStationMongoRepository extends MongoRepository<SubwayStationEntity, String> {

    Optional<SubwayStationEntity> findByName(String name);

    List<SubwayStationEntity> findByPointNear(Point center, Distance distance);

}
