package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.SubwayStationMap;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Map;
import java.util.Optional;

public interface SubwayStationRepository extends MongoRepository<SubwayStationMap,String> {

    default Optional<Map<String, SubwayStation>> findStationMap() {
        return findById("subway_map")
                .map(SubwayStationMap::getStationMap);
    }

    default void saveStationMap(Map<String, SubwayStation> stationMap) {
        SubwayStationMap entity = new SubwayStationMap(stationMap);
        save(entity);
    }
}
