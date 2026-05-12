package com.f12.moitz.application.subway.setup;

import com.f12.moitz.application.port.place.PlaceFinder;
import com.f12.moitz.application.port.subway.SubwayMapLoader;
import com.f12.moitz.application.port.subway.dto.RawRouteInfo;
import com.f12.moitz.application.subway.SubwayStationService;
import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.domain.subway.repository.SubwayEdgeRepository;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetupService {

    private final SubwayStationService subwayStationService;
    private final SubwayEdgeRepository subwayEdgeRepository;
    private final SubwayMapLoader subwayMapLoader;
    private final PlaceFinder placeFinder;
    private final SubwayEdgesBuilder subwayEdgesBuilder;
    private final SubwayMapSupplement subwayMapSupplement;

    public void setup() {
        log.info("SubwayEdges 초기화 시작");

        if (hasExistingSubwayMap()) {
            log.info("DB에 데이터가 있습니다. 저장된 데이터를 사용합니다. 서비스 시작.");
            return;
        }

        // TODO: 비교해서 필요한 것만 저장하도록 해야 할듯?

        final List<RawRouteInfo> rawRoutes = loadRawRoutes();
        final List<SubwayStation> subwayStations = createSubwayStations(rawRoutes);

        saveSubwayMap(rawRoutes, subwayStations);
        log.info("SubwayEdges 초기화 완료. 서비스 시작.");
    }

    private boolean hasExistingSubwayMap() {
        return subwayStationService.getCount() > 0 && subwayEdgeRepository.count() > 0;
    }

    private List<RawRouteInfo> loadRawRoutes() {
        return subwayMapLoader.loadRawRoutes();
    }

    private List<SubwayStation> createSubwayStations(final List<RawRouteInfo> rawRoutes) {
        final List<String> stationNames = extractStationNames(rawRoutes);
        return placeFinder.findPlacesByNames(stationNames)
                .stream()
                .map(place -> new SubwayStation(place.getName(), place.getPoint()))
                .toList();
    }

    private List<String> extractStationNames(final List<RawRouteInfo> rawRoutes) {
        return rawRoutes.stream()
                .flatMap(route -> route.paths().stream())
                .flatMap(path -> Stream.of(
                        path.departureStation().stationName(),
                        path.arrivalStation().stationName()
                ))
                .distinct()
                .toList();
    }

    private void saveSubwayMap(
            final List<RawRouteInfo> rawRoutes,
            final List<SubwayStation> subwayStations
    ) {
        subwayStationService.saveAll(subwayStations);
        final SubwayEdges subwayEdges = subwayEdgesBuilder.build(subwayStations, rawRoutes);
        subwayMapSupplement.apply(subwayEdges);
        subwayEdgeRepository.saveAll(subwayEdges.getSubwayEdges());
    }

}
