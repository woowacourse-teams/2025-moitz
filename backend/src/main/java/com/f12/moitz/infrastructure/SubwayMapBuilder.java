package com.f12.moitz.infrastructure;

import com.f12.moitz.domain.repository.SubwayStationRepository;
import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayStation;
import com.f12.moitz.infrastructure.client.open.OpenApiClient;
import com.f12.moitz.infrastructure.client.open.dto.PathResponse;
import com.f12.moitz.infrastructure.client.open.dto.SubwayRouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubwayMapBuilder {
    public static final String CSV_PATH = "src/main/resources/route-for-station-map.csv";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final SubwayStationRepository subwayStationRepository;
    private final OpenApiClient openApiClient;

    public Map<String, SubwayStation> buildAndSaveToMongo() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start("MongoDB 노선 만들기");

        log.info("MongoDB 기반 지하철 노선도 빌드 시작");

        final Map<String, SubwayStation> stationMap = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                final String[] tokens = line.split(",");
                if (tokens.length < 3) {
                    log.warn("CSV 라인 파싱 스킵: {}", line);
                    continue;
                }
                final String startName = tokens[1];
                final String endName = tokens[2];

                final SubwayRouteResponse response = openApiClient.searchMinimumTransferRoute(startName, endName);
                buildSubwayStations(response, stationMap);
            }

            addMissingData(stationMap);
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 읽기를 실패했습니다.", e);

        }
        stopWatch.stop();
        log.info("노선도 빌드 완료. 소요시간: {}ms, 총 {}개 역",
                stopWatch.getTotalTimeMillis(), stationMap.size());

        saveToMongoDB(stationMap);
        return stationMap;
    }


    private void saveToMongoDB(Map<String, SubwayStation> stationMap) {
        try {
            log.info("MongoDB에 {}개 역 저장 시작", stationMap.size());
            subwayStationRepository.deleteAll();
            subwayStationRepository.saveStationMap(stationMap);
            log.info("MongoDB 저장 완료 - 총 {}개 역", stationMap.size());

        } catch (Exception e) {
            log.error("MongoDB 저장 실패", e);
            throw new RuntimeException("MongoDB 저장에 실패했습니다.", e);
        }
    }


    private void buildSubwayStations(final SubwayRouteResponse response, final Map<String, SubwayStation> stationMap) {
        final List<PathResponse> paths = response.body().paths();

        for (int i = 0; i < paths.size(); i++) {
            final PathResponse currentPath = paths.get(i);
            final String fromName = getStationName(currentPath.dptreStn().stnNm());
            final String toName = getStationName(currentPath.arvlStn().stnNm());

            final String fromLine = getStationLine(currentPath.dptreStn().lineNm());
            final String toLine = getStationLine(currentPath.arvlStn().lineNm());

            final SubwayStation fromStation = stationMap.computeIfAbsent(fromName, SubwayStation::new);
            final SubwayStation toStation = stationMap.computeIfAbsent(toName, SubwayStation::new);

            final int distance = currentPath.stnSctnDstc();
            final int travelTimeInSeconds = calculateTravelTime(currentPath, paths, i);

            fromStation.addEdge(new Edge(toName, travelTimeInSeconds, distance, fromLine));

            if (currentPath.isTransfer()) {
                fromStation.addEdge(new Edge(toName, travelTimeInSeconds, distance, toLine));
            } else {
                final Edge reverseEdge = new Edge(fromName, travelTimeInSeconds, distance, fromLine);
                toStation.addEdge(reverseEdge);
            }
        }
    }

    private int calculateTravelTime(
            final PathResponse currentPath,
            final List<PathResponse> allPaths,
            final int currentIndex
    ) {
        // 환승 경로인 경우: 대기시간 + 소요시간 사용
        if (currentPath.isTransfer()) {
            return currentPath.wtngHr() + currentPath.reqHr();
        }

        // 일반 이동의 경우: 다음 경로의 출발시간 - 현재 경로의 출발시간
        if (currentIndex < allPaths.size() - 1) {
            final PathResponse nextPath = allPaths.get(currentIndex + 1);

            final String currentDepartureTime = currentPath.trainDptreTm();
            final String nextDepartureTime = nextPath.trainDptreTm();

            if (currentDepartureTime != null && nextDepartureTime != null) {
                return calculateTimeDifference(currentDepartureTime, nextDepartureTime);
            }
        }
        return currentPath.reqHr();
    }

    private int calculateTimeDifference(final String currentDepartureTime, final String nextDepartureTime) {
        try {
            final LocalTime current = LocalTime.parse(currentDepartureTime, TIME_FORMATTER);
            LocalTime next = LocalTime.parse(nextDepartureTime, TIME_FORMATTER);

            // 자정을 넘어가는 경우 고려
            if (next.isBefore(current)) {
                next = next.plusHours(24);
            }

            return (int) Duration.between(current, next).getSeconds();
        } catch (DateTimeException | ArithmeticException e) {
            throw new IllegalStateException("경로의 출발 시간을 파싱할 수 없습니다.");
        }
    }

    private String getStationName(final String name) {
        if ("이수".equals(name)) {
            return "총신대입구";
        }
        return name;
    }

    private String getStationLine(final String line) {
        if ("경의선".equals(line)) {
            return "경의중앙선";
        }
        return line;
    }

    private void addMissingData(final Map<String, SubwayStation> stationMap) {
        final SubwayStation joongrang = stationMap.get("중랑");
        joongrang.addEdge(new Edge("중랑", 1200, 0, "경의선"));
        joongrang.addEdge(new Edge("중랑", 1200, 0, "경춘선"));

        final SubwayStation euljiro4ga = stationMap.get("을지로4가");
        euljiro4ga.addEdge(new Edge("을지로4가", 550, 0, "2호선"));
        euljiro4ga.addEdge(new Edge("을지로4가", 550, 0, "5호선"));

        final SubwayStation cheongnyangni = stationMap.get("청량리");
        cheongnyangni.addEdge(new Edge("청량리", 1000, 0, "경춘선"));

        final SubwayStation oido = stationMap.get("오이도");
        oido.addEdge(new Edge("오이도", 500, 0, "4호선"));
        oido.addEdge(new Edge("오이도", 500, 0, "수인분당선"));

        final SubwayStation jeongwang = stationMap.get("정왕");
        jeongwang.addEdge(new Edge("정왕", 500, 0, "4호선"));
        jeongwang.addEdge(new Edge("정왕", 500, 0, "수인분당선"));

        final SubwayStation singiloncheon = stationMap.get("신길온천");
        singiloncheon.addEdge(new Edge("신길온천", 500, 0, "4호선"));
        singiloncheon.addEdge(new Edge("신길온천", 500, 0, "수인분당선"));

        final SubwayStation ansan = stationMap.get("안산");
        ansan.addEdge(new Edge("안산", 500, 0, "4호선"));
        ansan.addEdge(new Edge("안산", 500, 0, "수인분당선"));

        final SubwayStation choji = stationMap.get("초지");
        choji.addEdge(new Edge("초지", 500, 0, "4호선"));
        choji.addEdge(new Edge("초지", 500, 0, "수인분당선"));

        final SubwayStation gojan = stationMap.get("고잔");
        gojan.addEdge(new Edge("고잔", 500, 0, "4호선"));
        gojan.addEdge(new Edge("고잔", 500, 0, "수인분당선"));

        final SubwayStation jungang = stationMap.get("중앙");
        jungang.addEdge(new Edge("중앙", 500, 0, "4호선"));
        jungang.addEdge(new Edge("중앙", 500, 0, "수인분당선"));
    }
}
