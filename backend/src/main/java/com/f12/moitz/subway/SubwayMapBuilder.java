package com.f12.moitz.subway;

import com.f12.moitz.infrastructure.client.open.OpenApiClient;
import com.f12.moitz.infrastructure.client.open.dto.PathResponse;
import com.f12.moitz.infrastructure.client.open.dto.SubwayRouteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
@RequiredArgsConstructor
public class SubwayMapBuilder {

    public static final String CSV_PATH = "src/main/resources/route-for-station-map.csv";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final OpenApiClient openApiClient;
    private final ObjectMapper objectMapper;

    public Map<String, SubwayStation> build() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("노선도 만들기");
        Map<String, SubwayStation> stationMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] tokens = line.split(",");
                if (tokens.length < 3) {
                    log.warn("CSV 라인 파싱 스킵: {}", line);
                    continue;
                }
                String startName = tokens[1];
                String endName = tokens[2];

                SubwayRouteResponse subwayRouteResponse = openApiClient.searchMinimumTransferRoute(startName, endName);
                buildSubwayStations(subwayRouteResponse, stationMap);
            }

            addMissingData(stationMap);

        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 읽기를 실패했습니다.", e);
        }
        stopWatch.stop();
        log.debug(stopWatch.prettyPrint());

        String filePath = "src/main/resources/station-map.json";

        // 기존 파일 백업
        Path sourceFile = Paths.get(filePath);
        if (Files.exists(sourceFile)) {
            String backupFilePath = "src/main/resources/station-map-backup.json";
            Path backupFile = Paths.get(backupFilePath);
            try {
                Files.copy(sourceFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                log.debug("기존 station-map.json 파일을 station-map-backup.json으로 백업했습니다.");
            } catch (IOException e) {
                log.warn("백업 파일 생성에 실패했습니다: {}", e.getMessage());
            }
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // Java 8 시간 타입 지원을 위한 모듈 등록
            objectMapper.findAndRegisterModules();
            String jsonString = objectMapper.writeValueAsString(stationMap);
            fileWriter.write(jsonString);
            log.debug("JSON 파일 생성 완료");
        } catch (IOException e) {
            throw new RuntimeException("지하철 노선도 JSON 파싱에 실패했습니다.", e);
        }

        return stationMap;
    }

    private void buildSubwayStations(SubwayRouteResponse response, Map<String, SubwayStation> stationMap) {
        if (response == null || response.body() == null || response.body().paths() == null) {
            log.warn("SubwayRouteResponse 또는 경로 정보가 null입니다.");
            return;
        }
        List<PathResponse> paths = response.body().paths();

        for (int i = 0; i < paths.size(); i++) {
            PathResponse currentPath = paths.get(i);
            String fromName = getStationName(currentPath.dptreStn().stnNm());
            String toName = getStationName(currentPath.arvlStn().stnNm());

            SubwayStation fromStation = stationMap.computeIfAbsent(fromName, SubwayStation::new);
            SubwayStation toStation = stationMap.computeIfAbsent(toName, SubwayStation::new);

            int distance = currentPath.stnSctnDstc();
            int travelTimeInSeconds = calculateTravelTime(currentPath, paths, i);

            fromStation.addEdge(new Edge(toName, travelTimeInSeconds, distance, currentPath.dptreStn().lineNm()));

            if (currentPath.isTransfer()) {
                fromStation.addEdge(new Edge(toName, travelTimeInSeconds, distance, currentPath.arvlStn().lineNm()));
            } else {
                Edge reverseEdge = new Edge(fromName, travelTimeInSeconds, distance, currentPath.dptreStn().lineNm());
                toStation.addEdge(reverseEdge);
            }
        }
    }

    private int calculateTravelTime(PathResponse currentPath, List<PathResponse> allPaths, int currentIndex) {
        // 환승 경로인 경우: 대기시간 + 소요시간 사용
        if (currentPath.isTransfer()) {
            return currentPath.wtngHr() + currentPath.reqHr();
        }

        // 일반 이동의 경우: 다음 경로의 출발시간 - 현재 경로의 출발시간
        if (currentIndex < allPaths.size() - 1) {
            PathResponse nextPath = allPaths.get(currentIndex + 1);

            String currentDepartureTime = currentPath.trainDptreTm();
            String nextDepartureTime = nextPath.trainDptreTm();

            if (currentDepartureTime != null && nextDepartureTime != null) {
                return calculateTimeDifference(currentDepartureTime, nextDepartureTime);
            }
        }
        return currentPath.reqHr();
    }

    private int calculateTimeDifference(String currentDepartureTime, String nextDepartureTime) {
        try {
            LocalTime current = LocalTime.parse(currentDepartureTime, TIME_FORMATTER);
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

    private String getStationName(String name) {
        if ("이수".equals(name)) {
            return "총신대입구";
        }
        return name;
    }

    private void addMissingData(final Map<String, SubwayStation> stationMap) {
        SubwayStation joongrang = stationMap.get("중랑");
        joongrang.addEdge(new Edge("중랑", 1200, 0, "경의선"));
        joongrang.addEdge(new Edge("중랑", 1200, 0, "경춘선"));

        SubwayStation euljiro4ga = stationMap.get("을지로4가");
        euljiro4ga.addEdge(new Edge("을지로4가", 550, 0, "2호선"));
        euljiro4ga.addEdge(new Edge("을지로4가", 550, 0, "5호선"));

        SubwayStation cheongnyangni = stationMap.get("청량리");
        cheongnyangni.addEdge(new Edge("청량리", 1000, 0, "경춘선"));

        SubwayStation oido = stationMap.get("오이도");
        oido.addEdge(new Edge("오이도", 500, 0, "4호선"));
        oido.addEdge(new Edge("오이도", 500, 0, "수인분당선"));

        SubwayStation jeongwang = stationMap.get("정왕");
        jeongwang.addEdge(new Edge("정왕", 500, 0, "4호선"));
        jeongwang.addEdge(new Edge("정왕", 500, 0, "수인분당선"));

        SubwayStation singiloncheon = stationMap.get("신길온천");
        singiloncheon.addEdge(new Edge("신길온천", 500, 0, "4호선"));
        singiloncheon.addEdge(new Edge("신길온천", 500, 0, "수인분당선"));

        SubwayStation ansan = stationMap.get("안산");
        ansan.addEdge(new Edge("안산", 500, 0, "4호선"));
        ansan.addEdge(new Edge("안산", 500, 0, "수인분당선"));

        SubwayStation choji = stationMap.get("초지");
        choji.addEdge(new Edge("초지", 500, 0, "4호선"));
        choji.addEdge(new Edge("초지", 500, 0, "수인분당선"));

        SubwayStation gojan = stationMap.get("고잔");
        gojan.addEdge(new Edge("고잔", 500, 0, "4호선"));
        gojan.addEdge(new Edge("고잔", 500, 0, "수인분당선"));

        SubwayStation jungang = stationMap.get("중앙");
        jungang.addEdge(new Edge("중앙", 500, 0, "4호선"));
        jungang.addEdge(new Edge("중앙", 500, 0, "수인분당선"));
    }

}
