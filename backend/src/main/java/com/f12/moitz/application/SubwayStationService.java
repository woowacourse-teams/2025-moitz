package com.f12.moitz.application;

import com.f12.moitz.domain.CandidatePlaceSearchArea;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.SubwayStationName;
import com.f12.moitz.infrastructure.persistence.SubwayStationEntity;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayStationService {

    private static final int DISTANCE_VALUE = 10;

    private final SubwayStationRepository subwayStationRepository;

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
        final SubwayStationName subwayStationName = new SubwayStationName(name);
        return subwayStationName.getSearchNames().stream()
                .map(this::getSubwayStation)
                .flatMap(Optional::stream)
                .findFirst();
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
        final CandidatePlaceSearchArea searchArea = new CandidatePlaceSearchArea(startingStations, distanceValue);

        return subwayStationRepository.findByPointNear(
                        toGeoPoint(searchArea.getCenter()),
                        toDistance(searchArea)
                ).stream()
                .map(SubwayStationEntity::toSubwayStation)
                .toList();
    }

    private org.springframework.data.geo.Point toGeoPoint(final Point point) {
        return new org.springframework.data.geo.Point(point.getX(), point.getY());
    }

    private Distance toDistance(final CandidatePlaceSearchArea searchArea) {
        return new Distance(searchArea.getRadiusKilometers(), Metrics.KILOMETERS);
    }

}
