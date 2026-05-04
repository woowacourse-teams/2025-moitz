package com.f12.moitz.application.subway.setup;

import com.f12.moitz.application.port.PlaceFinder;
import com.f12.moitz.application.subway.SubwayStationService;
import com.f12.moitz.domain.subway.Edge;
import com.f12.moitz.domain.subway.SubwayEdges;
import com.f12.moitz.domain.subway.SubwayLine;
import com.f12.moitz.domain.subway.SubwayStation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubwayMapSupplement {

    private final SubwayStationService subwayStationService;
    private final PlaceFinder placeFinder;

    public void apply(final SubwayEdges subwayEdges) {
        log.debug("SubwayMapSupplement 적용 시작");

        addTransferEdges(subwayEdges);
        addCircularStations();
        addCircularEdges(subwayEdges);
    }

    private void addTransferEdges(final SubwayEdges subwayEdges) {
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
