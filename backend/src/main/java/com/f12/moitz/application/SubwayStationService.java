package com.f12.moitz.application;

import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubwayStationService {

    private final SubwayStationRepository subwayStationRepository;

    public List<SubwayStation> getAll() {
        return subwayStationRepository.findAll();
    }

    public List<String> findAllStationNames() {
        return subwayStationRepository.findAll()
                .stream()
                .map(SubwayStation::getName)
                .toList();
    }

    public SubwayStation getByName(final String name) {
        return subwayStationRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("이름이 일치하는 지하철역이 존재하지 않습니다. 역 이름: " + name));
    }

    public Optional<SubwayStation> findByName(final String name) {
        if ("총신대입구역".equals(name) || "이수역".equals(name)) {
            return subwayStationRepository.findByName("총신대입구(이수)역");
        }
        return subwayStationRepository.findByName(name);
    }

    public long getCount() {
        return subwayStationRepository.count();
    }

    public void saveAll(final List<SubwayStation> subwayStations) {
        subwayStationRepository.saveAll(subwayStations);
    }

}
