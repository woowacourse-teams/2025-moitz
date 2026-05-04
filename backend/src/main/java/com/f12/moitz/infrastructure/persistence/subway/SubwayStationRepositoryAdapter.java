package com.f12.moitz.infrastructure.persistence.subway;

import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.repository.SubwayStationRepository;
import com.f12.moitz.infrastructure.persistence.subway.repository.SubwayStationMongoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Repository;

@Repository
public class SubwayStationRepositoryAdapter implements SubwayStationRepository {

    private final SubwayStationMongoRepository subwayStationMongoRepository;

    public SubwayStationRepositoryAdapter(final SubwayStationMongoRepository subwayStationMongoRepository) {
        this.subwayStationMongoRepository = subwayStationMongoRepository;
    }

    @Override
    public List<SubwayStation> findAll() {
        return subwayStationMongoRepository.findAll().stream()
                .map(SubwayStationEntity::toSubwayStation)
                .toList();
    }

    @Override
    public Optional<SubwayStation> findByName(final String name) {
        return subwayStationMongoRepository.findByName(name)
                .map(SubwayStationEntity::toSubwayStation);
    }

    @Override
    public List<SubwayStation> findByPointNear(final Point center, final int radiusKilometers) {
        return subwayStationMongoRepository.findByPointNear(
                        toGeoPoint(center),
                        new Distance(radiusKilometers, Metrics.KILOMETERS)
                ).stream()
                .map(SubwayStationEntity::toSubwayStation)
                .toList();
    }

    @Override
    public long count() {
        return subwayStationMongoRepository.count();
    }

    @Override
    public void saveAll(final List<SubwayStation> subwayStations) {
        final List<SubwayStationEntity> subwayStationEntities = subwayStations.stream()
                .map(SubwayStationEntity::fromSubwayStation)
                .toList();
        subwayStationMongoRepository.saveAll(subwayStationEntities);
    }

    private org.springframework.data.geo.Point toGeoPoint(final Point point) {
        return new org.springframework.data.geo.Point(point.getX(), point.getY());
    }

}
