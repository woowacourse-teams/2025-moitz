package com.f12.moitz.application.subway;

import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import com.f12.moitz.domain.subway.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.SubwayStationName;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayStationService {

    private final SubwayStationRepository subwayStationRepository;

    public List<SubwayStation> getAll() {
        return subwayStationRepository.findAll();
    }

    public List<String> findAllStationNames() {
        return subwayStationRepository.findAll().stream()
                .map(Place::getName)
                .toList();
    }

    public SubwayStation getByName(final String name) {
        return subwayStationRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("이름이 일치하는 지하철역이 존재하지 않습니다. 역 이름: " + name));
    }

    public Optional<SubwayStation> findByName(final String name) {
        final SubwayStationName subwayStationName = new SubwayStationName(name);
        return subwayStationName.getSearchNames().stream()
                .map(this::getSubwayStation)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private Optional<SubwayStation> getSubwayStation(final String stationName) {
        return subwayStationRepository.findByName(stationName);
    }

    public long getCount() {
        return subwayStationRepository.count();
    }

    public void saveAll(final List<SubwayStation> subwayStations) {
        subwayStationRepository.saveAll(subwayStations);
    }

    public List<SubwayStation> findByPointNear(final Point center, final int radiusKilometers) {
        return subwayStationRepository.findByPointNear(center, radiusKilometers);
    }

}
