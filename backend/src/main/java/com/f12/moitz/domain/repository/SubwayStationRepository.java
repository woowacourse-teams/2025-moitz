package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.Optional;

public interface SubwayStationRepository {

    List<SubwayStation> findAll();

    Optional<SubwayStation> findByName(String name);

    List<SubwayStation> findByPointNear(Point center, int radiusKilometers);

    long count();

    void saveAll(List<SubwayStation> subwayStations);

}
