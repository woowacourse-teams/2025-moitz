package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.subway.SubwayStation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface SubwayStationRepository extends MongoRepository<SubwayStation,String> {

    default Map<String, SubwayStation> findAllAsMap() {
        return findAll().stream()
                .collect(Collectors.toMap(SubwayStation::getName, station -> station));
    }

    default void saveStationMap(final Map<String, SubwayStation> stationMap) {
        final List<SubwayStation> stations = stationMap.entrySet().stream()
                .map(entry -> {
                    SubwayStation station = entry.getValue();
                    if (station.getName() == null || !station.getName().equals(entry.getKey())) {
                        station.setName(entry.getKey());
                    }
                    return station;
                })
                .collect(Collectors.toList());

        saveAll(stations);
    }

}
