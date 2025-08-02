package com.f12.moitz.infrastructure.odsay;

import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.infrastructure.odsay.dto.SubPathResponse;
import com.f12.moitz.infrastructure.odsay.dto.SubwayRouteSearchResponse;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class OdsayMapper {

    public RouteResponse toRouteResponse(SubwayRouteSearchResponse odsayResponse) {
        var bestRoute = odsayResponse.result().path().stream()
                .min(Comparator.comparingInt(path -> path.info().totalTime()))
                .orElseThrow(() -> new IllegalStateException("경로 정보를 찾을 수 없습니다."));

        AtomicInteger index = new AtomicInteger(0);
        List<PathResponse> paths = bestRoute.subPath().stream()
                // TODO: 환승 경로까지 걸어가는 시간을 고려해야 함, 임시로 지하철 이동 소요 시간만
                .filter(subPathResponse -> subPathResponse.trafficType() == 1)
                .map(subPath -> new PathResponse(
                        index.getAndIncrement(),
                        subPath.startName(),
                        subPath.endName(),
                        (subPath.lane() == null || subPath.lane().isEmpty()) ? null : String.valueOf(subPath.lane().getFirst().subwayCode()),
                        subPath.sectionTime()
                ))
                .toList();

        final SubPathResponse start = bestRoute.subPath().stream()
                .filter(subPathResponse -> subPathResponse.trafficType() == 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("시작 지점의 경로 정보를 찾을 수 없습니다."));

        return new RouteResponse(
                start.startName(),
                String.valueOf(start.startX()),
                String.valueOf(start.startY()),
                String.valueOf(bestRoute.info().subwayTransitCount()),
                bestRoute.info().totalTime(),
                paths
        );
    }

}
