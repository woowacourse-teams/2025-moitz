package com.f12.moitz.application;

import com.f12.moitz.domain.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CsvRouteService {

    private final SubwayStationRepository subwayStationRepository;

    public void saveSubwayRoute(final String filename) {

    }
}
