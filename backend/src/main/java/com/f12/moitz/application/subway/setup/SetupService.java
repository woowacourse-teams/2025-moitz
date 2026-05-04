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

        // 지하철 노선도 데이터 존재하는지 확인
        if (subwayStationService.getCount() > 0 && subwayEdgeRepository.count() > 0) {
            log.info("DB에 데이터가 있습니다. 저장된 데이터를 사용합니다. 서비스 시작.");
            return;
        }

        // TODO: 비교해서 필요한 것만 저장하도록 해야 할듯?

        // 지하철 데이터 로딩
        final List<RawRouteInfo> rawRoutes = subwayMapLoader.loadRawRoutes();

        // 기존 역 데이터를 삭제하고 지하철 데이터로부터 새로운 역 데이터 저장 (?)
        final List<String> stationNames = rawRoutes.stream()
                .flatMap(route -> route.paths().stream())
                .flatMap(path -> Stream.of(
                        path.departureStation().stationName(),
                        path.arrivalStation().stationName()
                ))
                .distinct()
                .toList();

        final List<SubwayStation> subwayStations = placeFinder.findPlacesByNames(stationNames)
                .stream()
                .map(place -> new SubwayStation(place.getName(), place.getPoint()))
                .toList();

        subwayStationService.saveAll(subwayStations);

        // 역과 엣지 조립
        final SubwayEdges subwayEdges = subwayEdgesBuilder.build(subwayStations, rawRoutes);
        subwayMapSupplement.apply(subwayEdges);

        // 엣지 데이터 저장
        subwayEdgeRepository.saveAll(subwayEdges.getSubwayEdges());
        log.info("SubwayEdges 초기화 완료. 서비스 시작.");
    }

}
