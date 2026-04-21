package com.f12.moitz.application;

import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.persistence.SubwayStationEntity;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayStationService {

    private static final int DISTANCE_VALUE = 10;

    private final SubwayStationRepository subwayStationRepository;
    private final GeometryFactory geometryFactory;

    public List<SubwayStation> getAll() {
        return subwayStationRepository.findAll().stream()
                .map(SubwayStationEntity::toSubwayStation)
                .toList();
    }

    public List<String> findAllStationNames() {
        return subwayStationRepository.findAll().stream()
                .map(SubwayStationEntity::toSubwayStation)
                .map(Place::getName)
                .toList();
    }

    public SubwayStation getByName(final String name) {
        return subwayStationRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("이름이 일치하는 지하철역이 존재하지 않습니다. 역 이름: " + name))
                .toSubwayStation();
    }

    public Optional<SubwayStation> findByName(final String name) {
        if ("총신대입구역".equals(name) || "이수역".equals(name)) {
            return getSubwayStation("총신대입구(이수)역")
                    .or(() -> getSubwayStation(name));
        }
        return getSubwayStation(name);
    }

    private Optional<SubwayStation> getSubwayStation(final String stationName) {
        return subwayStationRepository.findByName(stationName)
                .map(SubwayStationEntity::toSubwayStation);
    }

    public long getCount() {
        return subwayStationRepository.count();
    }

    public void saveAll(final List<SubwayStation> subwayStations) {
        final List<SubwayStationEntity> subwayStationEntities = subwayStations.stream()
                .map(SubwayStationEntity::fromSubwayStation)
                .toList();
        subwayStationRepository.saveAll(subwayStationEntities);
    }

    public List<SubwayStation> generateCandidatePlace(final List<SubwayStation> startingStations) {
        return generateCandidatePlace(startingStations, DISTANCE_VALUE);
    }

    public List<SubwayStation> generateCandidatePlace(
            final List<SubwayStation> startingStations,
            final int distanceValue
    ) {
        final List<SubwayStationEntity> stationEntities = startingStations.stream()
                .map(SubwayStationEntity::fromSubwayStation)
                .toList();

        final Coordinate[] coordinateArr = getCoordinates(stationEntities);

        final org.springframework.data.geo.Point center = getCenterPoint(coordinateArr);
        final Distance distance = new Distance(distanceValue, Metrics.KILOMETERS);

        return subwayStationRepository.findByPointNear(center, distance).stream()
                .map(SubwayStationEntity::toSubwayStation)
                .toList();
    }

    private Coordinate[] getCoordinates(final List<SubwayStationEntity> stationEntities) {
        return stationEntities.stream()
                .map(place -> {
                    final List<Double> coordinates = place.getPoint().getCoordinates();
                    return new Coordinate(coordinates.get(0), coordinates.get(1));
                })
                .toArray(Coordinate[]::new);
    }

    private org.springframework.data.geo.Point getCenterPoint(final Coordinate[] coordinateArr) {
        final MultiPoint multiPoint = geometryFactory.createMultiPointFromCoords(coordinateArr);
        final Point centroid = multiPoint.getCentroid();
        return new org.springframework.data.geo.Point(centroid.getX(), centroid.getY());
    }

}
