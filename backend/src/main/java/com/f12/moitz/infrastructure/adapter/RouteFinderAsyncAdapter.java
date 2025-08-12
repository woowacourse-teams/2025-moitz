package com.f12.moitz.infrastructure.adapter;

import com.f12.moitz.application.port.RouteFinder;
import com.f12.moitz.application.port.dto.StartEndPair;
import com.f12.moitz.domain.Path;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.Route;
import com.f12.moitz.domain.TravelMethod;
import com.f12.moitz.infrastructure.client.odsay.OdsayMultiClient;
import com.f12.moitz.infrastructure.client.odsay.SubwayCode;
import com.f12.moitz.infrastructure.client.odsay.dto.SubPathResponse;
import com.f12.moitz.infrastructure.client.odsay.dto.SubwayRouteSearchResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteFinderAsyncAdapter implements RouteFinder {

    private final OdsayMultiClient odsayMultiClient;

    @Override
    public List<Route> findRoutes(final List<StartEndPair> placePairs) {
        return Flux.fromIterable(placePairs)
                .flatMapSequential(pair -> Mono.delay(Duration.ofMillis(1000))
                                .then(odsayMultiClient.getRoute(pair.start().getPoint(), pair.end().getPoint())),
                        5)
                .map(this::convertPaths)
                .map(Route::new)
                .collectList()
                .block();
    }

    // 해당 로직은 OdsayClient로 옮기는 게 좋을까?
    private List<Path> convertPaths(final SubwayRouteSearchResponse odsayResponse) {
        final var bestRoute = odsayResponse.result().path().stream()
                .min(Comparator.comparingInt(path -> path.info().totalTime()))
                .orElseThrow(() -> new IllegalStateException("경로 정보를 찾을 수 없습니다."));

        // 시작과 끝에 도보로 걸어가는 응답 제거
        final List<SubPathResponse> subPaths = bestRoute.subPath();
        if (!subPaths.isEmpty() && subPaths.getFirst().trafficType() != 1) {
            subPaths.removeFirst();
        }
        if (!subPaths.isEmpty() && subPaths.getLast().trafficType() != 1) {
            subPaths.removeLast();
        }

        final List<Path> resultingPaths = new ArrayList<>();
        // 환승이 존재할 경우 마지막 유효한 역 이름과 좌표로 대치하도록
        String lastValidStationName = null;
        Point lastValidPoint = null;

        for (SubPathResponse subPath : subPaths) {
            if (subPath.startName() == null || subPath.endName() == null) {
                // 환승인 경우
                if (lastValidStationName != null) {
                    final Place transferPlace = new Place(lastValidStationName, lastValidPoint);
                    resultingPaths.add(new Path(
                            transferPlace,
                            transferPlace,
                            TravelMethod.TRANSFER,
                            // TODO: 환승 이동 시간 계산 고려
                            3,
                            null
                    ));
                }
            }
            else {
                final Place startPlace = new Place(
                        subPath.startName(),
                        new Point(subPath.startX(), subPath.startY())
                );
                final Place endPlace = new Place(
                        subPath.endName(),
                        new Point(subPath.endX(), subPath.endY())
                );
                resultingPaths.add(new Path(
                        startPlace,
                        endPlace,
                        TravelMethod.from(subPath.trafficType()),
                        subPath.sectionTime(),
                        SubwayCode.fromCode(subPath.lane().getFirst().subwayCode()).getTitle()
                ));

                lastValidStationName = subPath.endName();
                lastValidPoint = new Point(subPath.endX(), subPath.endY());
            }
        }

        return resultingPaths;
    }

}
