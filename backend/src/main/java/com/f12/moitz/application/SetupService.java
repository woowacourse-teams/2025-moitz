package com.f12.moitz.application;

import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.application.port.SubwayMapLoader;
import com.f12.moitz.application.port.dto.RawPathInfo;
import com.f12.moitz.application.port.dto.RawRouteInfo;
import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetupService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final SubwayStationService subwayStationService;
    private final SubwayEdgeService subwayEdgeService;
    private final SubwayMapLoader subwayMapLoader;
    private final PlaceFinder placeFinder;

    public void setup() {
        log.info("SubwayEdges 초기화 시작");

        // 지하철 노선도 데이터 존재하는지 확인
        if (subwayStationService.getCount() > 0 && subwayEdgeService.getCount() > 0) {
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
        final SubwayEdges subwayEdges = assembleSubwayStations(subwayStations, rawRoutes);

        // 엣지 데이터 저장
        subwayEdgeService.saveAll(subwayEdges);
        log.info("SubwayEdges 초기화 완료. 서비스 시작.");
    }

    private SubwayEdges assembleSubwayStations(final List<SubwayStation> stations, final List<RawRouteInfo> rawRoutes) {
        final Map<SubwayStation, List<Edge>> stationMap = new HashMap<>();
        for (RawRouteInfo rawRoute : rawRoutes) {
            final List<RawPathInfo> paths = rawRoute.paths();
            for (int i = 0; i < paths.size(); i++) {
                final RawPathInfo currentPath = paths.get(i);
                final String fromName = currentPath.departureStation().stationName();
                final String toName = currentPath.arrivalStation().stationName();
                final String fromLine = currentPath.departureStation().lineName();
                final String toLine = currentPath.arrivalStation().lineName();

                final SubwayStation fromStation = getStationByName(stations, fromName);
                final SubwayStation toStation = getStationByName(stations, toName);

                final int distance = currentPath.stationSectionDistance();
                final int travelTimeInSeconds = calculateTravelTime(currentPath, paths, i);

                final List<Edge> fromEdges = stationMap.computeIfAbsent(fromStation, k -> new ArrayList<>());
                final List<Edge> toEdges = stationMap.computeIfAbsent(toStation, k -> new ArrayList<>());

                fromEdges.add(new Edge(toStation, travelTimeInSeconds, distance, fromLine));
                if (currentPath.isTransfer()) {
                    fromEdges.add(new Edge(toStation, travelTimeInSeconds, distance, toLine));
                } else {
                    toEdges.add(new Edge(fromStation, travelTimeInSeconds, distance, fromLine));
                }
            }
        }
        final SubwayEdges subwayEdges = SubwayEdges.of(stationMap);
        addMissingData(subwayEdges);
        return subwayEdges;
    }

    private SubwayStation getStationByName(final List<SubwayStation> stations, final String fromName) {
        return stations.stream()
                .filter(station -> station.getName().equals(fromName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("이름이 일치하는 SubwayStation이 존재하지 않습니다: " + fromName));
    }

    private int calculateTravelTime(
            final RawPathInfo currentPath,
            final List<RawPathInfo> allPaths,
            final int currentIndex
    ) {
        if (currentPath.isTransfer()) {
            return currentPath.waitingSeconds() + currentPath.requiredSeconds();
        }

        if (currentIndex < allPaths.size() - 1) {
            final RawPathInfo nextPath = allPaths.get(currentIndex + 1);
            final String currentDepartureTime = currentPath.trainDepartureTime();
            final String nextDepartureTime = nextPath.trainDepartureTime();

            if (currentDepartureTime != null && nextDepartureTime != null) {
                return calculateTimeDifference(currentDepartureTime, nextDepartureTime);
            }
        }
        return currentPath.requiredSeconds();
    }

    private int calculateTimeDifference(final String currentDepartureTime, final String nextDepartureTime) {
        try {
            final LocalTime current = LocalTime.parse(currentDepartureTime, TIME_FORMATTER);
            LocalTime next = LocalTime.parse(nextDepartureTime, TIME_FORMATTER);
            if (next.isBefore(current)) {
                next = next.plusHours(24);
            }
            return (int) Duration.between(current, next).getSeconds();
        } catch (DateTimeException | ArithmeticException e) {
            throw new IllegalStateException("경로의 출발 시간을 파싱할 수 없습니다.");
        }
    }

    private void addMissingData(final SubwayEdges subwayEdges) {
        log.debug("addMissingData 호출됨. 추가 데이터 처리 시작");

        final SubwayStation joongrang = subwayEdges.getStationByName("중랑역");
        subwayEdges.addEdge(joongrang, new Edge(joongrang, 1200, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(joongrang, new Edge(joongrang, 1200, 0, SubwayLine.GYEONGCHUN.getTitle()));
        log.debug("중랑역 환승 Edge 추가됨: 경의중앙선, 경춘선");

        final SubwayStation euljiro4ga = subwayEdges.getStationByName("을지로4가역");
        subwayEdges.addEdge(euljiro4ga, new Edge(euljiro4ga, 550, 0, SubwayLine.SEOUL_METRO_LINE2.getTitle()));
        subwayEdges.addEdge(euljiro4ga, new Edge(euljiro4ga, 550, 0, SubwayLine.SEOUL_METRO_LINE5.getTitle()));
        log.debug("을지로4가역 환승 Edge 추가됨: 2호선, 5호선");

        final SubwayStation cheongnyangni = subwayEdges.getStationByName("청량리역");
        subwayEdges.addEdge(cheongnyangni, new Edge(cheongnyangni, 1000, 0, SubwayLine.GYEONGCHUN.getTitle()));
        log.debug("청량리역 환승 Edge 추가됨: 경춘선");

        final SubwayStation oido = subwayEdges.getStationByName("오이도역");
        subwayEdges.addEdge(oido, new Edge(oido, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(oido, new Edge(oido, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("오이도역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation jeongwang = subwayEdges.getStationByName("정왕역");
        subwayEdges.addEdge(jeongwang, new Edge(jeongwang, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(jeongwang, new Edge(jeongwang, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("정왕역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation singiloncheon = subwayEdges.getStationByName("신길온천역");
        subwayEdges.addEdge(singiloncheon, new Edge(singiloncheon, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(singiloncheon, new Edge(singiloncheon, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("신길온천역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation ansan = subwayEdges.getStationByName("안산역");
        subwayEdges.addEdge(ansan, new Edge(ansan, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(ansan, new Edge(ansan, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("안산역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation choji = subwayEdges.getStationByName("초지역");
        subwayEdges.addEdge(choji, new Edge(choji, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(choji, new Edge(choji, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("초지역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation gojan = subwayEdges.getStationByName("고잔역");
        subwayEdges.addEdge(gojan, new Edge(gojan, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(gojan, new Edge(gojan, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("고잔역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation jungang = subwayEdges.getStationByName("중앙역");
        subwayEdges.addEdge(jungang, new Edge(jungang, 500, 0, SubwayLine.SEOUL_METRO_LINE4.getTitle()));
        subwayEdges.addEdge(jungang, new Edge(jungang, 500, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("중앙역 환승 Edge 추가됨: 4호선, 수인분당선");

        final SubwayStation neunggok = subwayEdges.getStationByName("능곡역");
        subwayEdges.addEdge(neunggok, new Edge(neunggok, 600, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(neunggok, new Edge(neunggok, 600, 0, SubwayLine.SEOHAE_LINE.getTitle()));
        log.debug("능곡역 환승 Edge 추가됨: 경의중앙선, 서해선");

        final SubwayStation daegok = subwayEdges.getStationByName("대곡역");
        subwayEdges.addEdge(daegok, new Edge(daegok, 600, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(daegok, new Edge(daegok, 600, 0, SubwayLine.SEOHAE_LINE.getTitle()));
        subwayEdges.addEdge(daegok, new Edge(daegok, 300, 0, SubwayLine.GTX_A.getTitle()));
        log.debug("대곡역 환승 Edge 추가됨: 경의중앙선, 서해선, GTX-A");

        final SubwayStation goksan = subwayEdges.getStationByName("곡산역");
        subwayEdges.addEdge(goksan, new Edge(goksan, 600, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(goksan, new Edge(goksan, 600, 0, SubwayLine.SEOHAE_LINE.getTitle()));
        log.debug("곡산역 환승 Edge 추가됨: 경의중앙선, 서해선");

        final SubwayStation baengma = subwayEdges.getStationByName("백마역");
        subwayEdges.addEdge(baengma, new Edge(baengma, 600, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(baengma, new Edge(baengma, 600, 0, SubwayLine.SEOHAE_LINE.getTitle()));
        log.debug("백마역 환승 Edge 추가됨: 경의중앙선, 서해선");

        final SubwayStation pungsan = subwayEdges.getStationByName("풍산역");
        subwayEdges.addEdge(pungsan, new Edge(pungsan, 600, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(pungsan, new Edge(pungsan, 600, 0, SubwayLine.SEOHAE_LINE.getTitle()));
        log.debug("풍산역 환승 Edge 추가됨: 경의중앙선, 서해선");

        final SubwayStation ilsan = subwayEdges.getStationByName("일산역");
        subwayEdges.addEdge(ilsan, new Edge(ilsan, 600, 0, SubwayLine.GYEONGUI_JOUNGANG.getTitle()));
        subwayEdges.addEdge(ilsan, new Edge(ilsan, 600, 0, SubwayLine.SEOHAE_LINE.getTitle()));
        log.debug("일산역 환승 Edge 추가됨: 경의중앙선, 서해선");

        final SubwayStation pangyo = subwayEdges.getStationByName("판교역");
        subwayEdges.addEdge(pangyo, new Edge(pangyo, 180, 0, SubwayLine.SIN_BUNDANG.getTitle()));
        subwayEdges.addEdge(pangyo, new Edge(pangyo, 180, 0, SubwayLine.GYEONGGANG.getTitle()));
        log.debug("판교역 환승 Edge 추가됨: 신분당선, 경강선");

        final SubwayStation seongnam = subwayEdges.getStationByName("성남역");
        subwayEdges.addEdge(seongnam, new Edge(seongnam, 240, 0, SubwayLine.GTX_A.getTitle()));
        subwayEdges.addEdge(seongnam, new Edge(seongnam, 240, 0, SubwayLine.GYEONGGANG.getTitle()));
        log.debug("성남역 환승 Edge 추가됨: GTX-A, 경강선");

        final SubwayStation guseong = subwayEdges.getStationByName("구성역");
        subwayEdges.addEdge(guseong, new Edge(guseong, 240, 0, SubwayLine.GTX_A.getTitle()));
        subwayEdges.addEdge(guseong, new Edge(guseong, 240, 0, SubwayLine.SUIN_BUNDANG.getTitle()));
        log.debug("구성역 환승 Edge 추가됨: GTX-A, 수인분당선");

        final SubwayStation suseo = subwayEdges.getStationByName("수서역");
        subwayEdges.addEdge(suseo, new Edge(suseo, 240, 0, SubwayLine.GTX_A.getTitle()));
        log.debug("수서역 환승 Edge 추가됨: GTX-A");

        final SubwayStation seoulStation = subwayEdges.getStationByName("서울역");
        subwayEdges.addEdge(seoulStation, new Edge(seoulStation, 300, 0, SubwayLine.GTX_A.getTitle()));
        log.debug("서울역 환승 Edge 추가됨: GTX-A");

        addCircularStations();
        addCircularEdges(subwayEdges);
    }

    private void addCircularStations() {
        final List<SubwayStation> missingStations = placeFinder.findPlacesByNames(List.of("역촌역", "독바위역", "구산역"))
                .stream()
                .map(place -> new SubwayStation(place.getName(), place.getPoint()))
                .toList();

        subwayStationService.saveAll(missingStations);
        log.debug("6호선 순환 구간 SubwayStation 추가됨: 역촌역, 독바위역, 구산역");
    }

    private void addCircularEdges(final SubwayEdges subwayEdges) {
        final SubwayStation eungam = subwayEdges.getStationByName("응암역");
        final SubwayStation yeokchon = subwayStationService.getByName("역촌역");
        final SubwayStation bulgwang = subwayEdges.getStationByName("불광역");
        final SubwayStation dokbawi = subwayStationService.getByName("독바위역");
        final SubwayStation yeonsinnae = subwayEdges.getStationByName("연신내역");
        final SubwayStation gusan = subwayStationService.getByName("구산역");

        subwayEdges.addEdge(eungam, new Edge(yeokchon, 120, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        subwayEdges.addEdge(yeokchon, new Edge(bulgwang, 120, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        subwayEdges.addEdge(bulgwang, new Edge(dokbawi, 110, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        subwayEdges.addEdge(dokbawi, new Edge(yeonsinnae, 170, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        subwayEdges.addEdge(yeonsinnae, new Edge(gusan, 170, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        subwayEdges.addEdge(gusan, new Edge(eungam, 150, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        log.debug("6호선 순환 구간 Edge 추가됨: 응암 -> 역촌 -> 불광 -> 독바위 -> 연신내 -> 구산 -> 응암");

        subwayEdges.addEdge(bulgwang, new Edge(bulgwang, 180, 0, SubwayLine.SEOUL_METRO_LINE3.getTitle()));
        subwayEdges.addEdge(bulgwang, new Edge(bulgwang, 180, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        log.debug("불광역 환승 Edge 추가됨: 3호선, 6호선");

        subwayEdges.addEdge(yeonsinnae, new Edge(yeonsinnae, 360, 0, SubwayLine.GTX_A.getTitle()));
        subwayEdges.addEdge(yeonsinnae, new Edge(yeonsinnae, 390, 0, SubwayLine.SEOUL_METRO_LINE3.getTitle()));
        subwayEdges.addEdge(yeonsinnae, new Edge(yeonsinnae, 390, 0, SubwayLine.SEOUL_METRO_LINE6.getTitle()));
        log.debug("연신내역 환승 Edge 추가됨: GTX-A, 3호선, 6호선");
    }

}
