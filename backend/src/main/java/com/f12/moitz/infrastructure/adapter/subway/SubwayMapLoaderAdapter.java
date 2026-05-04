package com.f12.moitz.infrastructure.adapter.subway;

import com.f12.moitz.application.port.subway.SubwayMapLoader;
import com.f12.moitz.application.port.subway.dto.RawPathInfo;
import com.f12.moitz.application.port.subway.dto.RawRouteInfo;
import com.f12.moitz.application.port.subway.dto.RawStationInfo;
import com.f12.moitz.infrastructure.client.open.OpenApiClient;
import com.f12.moitz.infrastructure.client.open.dto.PathResponse;
import com.f12.moitz.infrastructure.client.open.dto.SubwayRouteResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubwayMapLoaderAdapter implements SubwayMapLoader {

    public static final String CSV_PATH = "src/main/resources/route-for-station-map.csv";

    private final OpenApiClient openApiClient;

    @Override
    public List<RawRouteInfo> loadRawRoutes() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start("외부 API 호출 및 DTO 변환");
        log.info("외부 API 기반 지하철 노선 정보 로드 시작");

        final List<RawRouteInfo> rawRoutes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
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
                rawRoutes.add(toRawRouteInfo(response));
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 읽기를 실패했습니다.", e);
        }

        stopWatch.stop();
        log.info("외부 API 정보 로드 및 변환 완료. 소요시간: {}ms", stopWatch.getTotalTimeMillis());
        return rawRoutes;
    }

    private RawRouteInfo toRawRouteInfo(final SubwayRouteResponse response) {
        final List<RawPathInfo> rawPaths = response.body().paths().stream()
                .map(this::toRawPathInfo)
                .collect(Collectors.toList());
        return new RawRouteInfo(rawPaths);
    }

    private RawPathInfo toRawPathInfo(final PathResponse pathResponse) {
        return new RawPathInfo(
                new RawStationInfo(
                        pathResponse.departureStation().stationName(),
                        pathResponse.departureStation().lineName()
                ),
                new RawStationInfo(
                        pathResponse.arrivalStation().stationName(),
                        pathResponse.arrivalStation().lineName()
                ),
                pathResponse.stationSectionDistance(),
                pathResponse.isTransfer(),
                pathResponse.waitingSeconds(),
                pathResponse.requiredSeconds(),
                pathResponse.trainDepartureTime()
        );
    }

}
