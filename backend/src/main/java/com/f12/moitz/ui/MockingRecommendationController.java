package com.f12.moitz.ui;

import com.f12.moitz.application.dto.MockRecommendationResponse;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import com.f12.moitz.application.dto.SubwayStationResponse;
import com.f12.moitz.domain.RecommendCondition;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendations")
public class MockingRecommendationController implements SwaggerMockingRecommendationController {

    @PostMapping("/test")
    public ResponseEntity<RecommendationCreateResponse> mockRecommend(@RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(new RecommendationCreateResponse("123"));
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<MockRecommendationResponse> mockGetRecommendation(@PathVariable("id") String id) {
        return ResponseEntity.ok(mock());
    }

    private MockRecommendationResponse mock() {
        return new MockRecommendationResponse(
                List.of("식당","카페","PC방"),
                List.of(
                        new StartingPlaceResponse(1L, 1, 127.094741101863, 37.5351180385975, "강변역"),
                        new StartingPlaceResponse(2L, 2, 127.01063381083677, 37.571669405802616, "동대문역"),
                        new StartingPlaceResponse(3L, 3, 126.952713197762, 37.4812845080678, "서울대입구역")
                ),
                List.of(
                        new RecommendationResponse(1L, 1, 37.54040751726388, 127.06920291650829, "건대입구역", 18, true,
                                "다양한 즐길거리가 가득! 🥳 (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "육일관", "식당", 1,
                                                        "http://place.map.kakao.com/1050190897"
                                                )
                                        ),
                                        RecommendCondition.CAFE, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "바나프레소 건대역점", "카페", 1,
                                                        "http://place.map.kakao.com/666587821"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "포포PC방 건대점", "PC방", 1,
                                                        "http://place.map.kakao.com/356960076"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 4, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975,
                                                        "건대입구", 127.06920291650829, 37.54040751726388, "2호선", 4)
                                        ), List.of(
                                                new SubwayStationResponse(1, "강변역", 127.094741101863, 37.5351180385975),
                                                new SubwayStationResponse(2, "구의역", 127.086180837795, 37.5371752725594),
                                                new SubwayStationResponse(3, "건대입구역", 127.06920291650829, 37.54040751726388)
                                        )),
                                        new RouteResponse(2, 1, 18, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "건대입구",
                                                        127.06920291650829, 37.54040751726388, "2호선", 13)
                                        ), List.of(
                                                new SubwayStationResponse(1, "동대문역", 127.01063381083677, 37.571669405802616),
                                                new SubwayStationResponse(2, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802),
                                                new SubwayStationResponse(3, "신당역", 127.019477533278, 37.5656730531732),
                                                new SubwayStationResponse(4, "상왕십리역", 127.02927241035283, 37.56443666620397),
                                                new SubwayStationResponse(5, "왕십리역", 127.03710337610202, 37.561268363317176),
                                                new SubwayStationResponse(6, "한양대역", 127.043639802768, 37.5557159860408),
                                                new SubwayStationResponse(7, "뚝섬역", 127.04738727881, 37.547241554679),
                                                new SubwayStationResponse(8, "성수역", 127.056066999327, 37.5445888153751),
                                                new SubwayStationResponse(9, "건대입구역", 127.06920291650829, 37.54040751726388)
                                        )),
                                        new RouteResponse(3, 0, 33, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "건대입구",
                                                        127.06920291650829, 37.54040751726388, "2호선", 33)
                                        ), List.of(
                                                new SubwayStationResponse(1, "서울대입구역", 126.952713197762, 37.4812845080678),
                                                new SubwayStationResponse(2, "낙성대역", 126.963523905001, 37.4770932409965),
                                                new SubwayStationResponse(3, "사당역", 126.98155858357366, 37.47656223234824),
                                                new SubwayStationResponse(4, "방배역", 126.997553345516, 37.4814561268152),
                                                new SubwayStationResponse(5, "서초역", 127.007662120039, 37.4918499338918),
                                                new SubwayStationResponse(6, "교대역", 127.013867969161, 37.4927431676548),
                                                new SubwayStationResponse(7, "강남역", 127.02800140627488, 37.49808633653005),
                                                new SubwayStationResponse(8, "역삼역", 127.03646946847, 37.5006744185994),
                                                new SubwayStationResponse(9, "선릉역", 127.04896282498558, 37.504497373023206),
                                                new SubwayStationResponse(10, "삼성역", 127.06302321147605, 37.508822740225305),
                                                new SubwayStationResponse(11, "종합운동장역", 127.073849447402, 37.5111446632705),
                                                new SubwayStationResponse(12, "잠실새내역", 127.086314327913, 37.5116263587296),
                                                new SubwayStationResponse(13, "잠실역", 127.10023101886318, 37.51331105877401),
                                                new SubwayStationResponse(14, "잠실나루역", 127.103808749487, 37.5207124124456),
                                                new SubwayStationResponse(15, "강변역", 127.094741101863, 37.5351180385975),
                                                new SubwayStationResponse(16, "구의역", 127.086180837795, 37.5371752725594),
                                                new SubwayStationResponse(17, "건대입구역", 127.06920291650829, 37.54040751726388)
                                        ))
                                )
                        ),
                        new RecommendationResponse(2L, 2, 37.47656223234824, 126.98155858357366, "사당역", 18, true,
                                "만남의 광장, 맛집도 다양! 😋 (식당, 카페, PC방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT,List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "훈장골 사당점", "식당", 1,
                                                        "http://place.map.kakao.com/63778027"
                                                )
                                        ),
                                        RecommendCondition.CAFE,List.of(
                                                new PlaceRecommendResponse(
                                                        2, 127.094741101863, 37.5351180385975,
                                                        "스타벅스 사당점", "카페", 1,
                                                        "http://place.map.kakao.com/23447734"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE,List.of(
                                                new PlaceRecommendResponse(
                                                        3, 127.094741101863, 37.5351180385975,
                                                        "레벨업PC방 사당역점", "PC방", 2,
                                                        "http://place.map.kakao.com/1705311839"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 25, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 25)
                                        ), List.of(
                                                new SubwayStationResponse(1, "강변역", 127.094741101863, 37.5351180385975),
                                                new SubwayStationResponse(2, "잠실나루역", 127.103808749487, 37.5207124124456),
                                                new SubwayStationResponse(3, "잠실역", 127.10023101886318, 37.51331105877401),
                                                new SubwayStationResponse(4, "잠실새내역", 127.086314327913, 37.5116263587296),
                                                new SubwayStationResponse(5, "종합운동장역", 127.073849447402, 37.5111446632705),
                                                new SubwayStationResponse(6, "삼성역", 127.06302321147605, 37.508822740225305),
                                                new SubwayStationResponse(7, "선릉역", 127.04896282498558, 37.504497373023206),
                                                new SubwayStationResponse(8, "역삼역", 127.03646946847, 37.5006744185994),
                                                new SubwayStationResponse(9, "강남역", 127.02800140627488, 37.49808633653005),
                                                new SubwayStationResponse(10, "교대역", 127.013867961, 37.4927431676548),
                                                new SubwayStationResponse(11, "서초역", 127.007662120039, 37.4918499338918),
                                                new SubwayStationResponse(12, "방배역", 126.997553345516, 37.4814561268152),
                                                new SubwayStationResponse(13, "사당역", 126.98155858357366, 37.47656223234824)
                                        )),
                                        new RouteResponse(2, 0, 25, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616, "사당",
                                                        126.98155858357366, 37.47656223234824, "4호선", 25)
                                        ), List.of(
                                                new SubwayStationResponse(1, "동대문역", 127.01063381083677, 37.571669405802616),
                                                new SubwayStationResponse(2, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802),
                                                new SubwayStationResponse(3, "충무로역", 126.99414960395544, 37.56139658395457),
                                                new SubwayStationResponse(4, "명동역", 126.98640235001736, 37.56096526943837),
                                                new SubwayStationResponse(5, "회현역", 126.9784372569283, 37.55876114587941),
                                                new SubwayStationResponse(6, "서울역", 126.96974961781686, 37.55332892758497),
                                                new SubwayStationResponse(7, "숙대입구역", 126.972118127373, 37.5445952301115),
                                                new SubwayStationResponse(8, "삼각지역", 126.972922951307, 37.5344393447708),
                                                new SubwayStationResponse(9, "신용산역", 126.967965039183, 37.5292591489375),
                                                new SubwayStationResponse(10, "이촌역", 126.97354382085399, 37.52241291408466),
                                                new SubwayStationResponse(11, "동작역", 126.97854611382496, 37.502744106282044),
                                                new SubwayStationResponse(12, "총신대입구(이수)역", 126.982211871752, 37.4867995957995),
                                                new SubwayStationResponse(13, "사당역", 126.98155858357366, 37.47656223234824)
                                        )),
                                        new RouteResponse(3, 0, 4, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 4)
                                        ), List.of(
                                                new SubwayStationResponse(1, "서울대입구역", 126.952713197762, 37.4812845080678),
                                                new SubwayStationResponse(2, "낙성대역", 126.963523905001, 37.4770932409965),
                                                new SubwayStationResponse(3, "사당역", 126.98155858357366, 37.47656223234824)
                                        ))
                                )
                        ),
                        new RecommendationResponse(3L, 3, 37.561268363317176, 127.03710337610202, "왕십리역", 18, true,
                                "교통 요충지, 엔터-식사 해결! ✨ (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT,List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "스시도쿠", "식당", 1,
                                                        "http://place.map.kakao.com/26792732"
                                                )
                                        ),
                                        RecommendCondition.CAFE,List.of(
                                                new PlaceRecommendResponse(
                                                        2, 127.094741101863, 37.5351180385975,
                                                        "탐앤탐스 왕십리역점", "카페", 1,
                                                        "http://place.map.kakao.com/10809505"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE,List.of(
                                                new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                        "이스포츠PC방 왕십리점", "PC방", 2,
                                                        "http://place.map.kakao.com/12326220"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 11, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "2호선", 11)
                                        ), List.of(
                                                new SubwayStationResponse(1, "강변역", 127.094741101863, 37.5351180385975),
                                                new SubwayStationResponse(2, "구의역", 127.086180837795, 37.5371752725594),
                                                new SubwayStationResponse(3, "건대입구역", 127.06920291650829, 37.54040751726388),
                                                new SubwayStationResponse(4, "성수역", 127.056066999327, 37.5445888153751),
                                                new SubwayStationResponse(5, "뚝섬역", 127.04738727881, 37.547241554679),
                                                new SubwayStationResponse(6, "한양대역", 127.043639802768, 37.5557159860408),
                                                new SubwayStationResponse(7, "왕십리역", 127.03710337610202, 37.561268363317176),
                                                new SubwayStationResponse(8, "상왕십리역", 127.02927241035283, 37.56443666620397),
                                                new SubwayStationResponse(9, "신당역", 127.019477533278, 37.5656730531732),
                                                new SubwayStationResponse(10, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802)
                                        )),
                                        new RouteResponse(2, 1, 11, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "2호선", 6)
                                        ), List.of(
                                                new SubwayStationResponse(1, "동대문역", 127.01063381083677, 37.571669405802616),
                                                new SubwayStationResponse(2, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802),
                                                new SubwayStationResponse(3, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802),
                                                new SubwayStationResponse(4, "신당역", 127.019477533278, 37.5656730531732),
                                                new SubwayStationResponse(5, "상왕십리역", 127.02927241035283, 37.56443666620397),
                                                new SubwayStationResponse(6, "왕십리역", 127.03710337610202, 37.561268363317176)
                                        )),
                                        new RouteResponse(3, 1, 32, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "선릉",
                                                        127.049271, 37.504577, "2호선", 16),
                                                new PathResponse(2, "선릉", 127.049271, 37.504577, "선릉", 127.049271,
                                                        37.504577, null, 3),
                                                new PathResponse(3, "선릉", 127.049271, 37.504577, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "수인분당선", 13)
                                        ), List.of(
                                                new SubwayStationResponse(1, "서울대입구역", 126.952713197762, 37.4812845080678),
                                                new SubwayStationResponse(2, "낙성대역", 126.963523905001, 37.4770932409965),
                                                new SubwayStationResponse(3, "사당역", 126.98155858357366, 37.47656223234824),
                                                new SubwayStationResponse(4, "방배역", 126.997553345516, 37.4814561268152),
                                                new SubwayStationResponse(5, "서초역", 127.007662120039, 37.4918499338918),
                                                new SubwayStationResponse(6, "교대역", 127.013867969161, 37.4927431676548),
                                                new SubwayStationResponse(7, "강남역", 127.02800140627488, 37.49808633653005),
                                                new SubwayStationResponse(8, "역삼역", 127.03646946847, 37.5006744185994),
                                                new SubwayStationResponse(9, "선릉역", 127.04896282498558, 37.504497373023206),
                                                new SubwayStationResponse(10, "선릉역", 127.04896282498558, 37.504497373023206),
                                                new SubwayStationResponse(11, "선정릉역", 127.043627289129, 37.5109326388803),
                                                new SubwayStationResponse(12, "강남구청역", 127.0413109462156, 37.51721617197854),
                                                new SubwayStationResponse(13, "압구정로데오역", 127.0406027693898, 37.5275184818021),
                                                new SubwayStationResponse(14, "서울숲역", 127.044746216358, 37.543645796605),
                                                new SubwayStationResponse(15, "왕십리역", 127.03710337610202, 37.561268363317176)
                                        ))
                                )
                        ),
                        new RecommendationResponse(4L, 4, 37.570227990912244, 126.98315081716676, "종각역", 24, false,
                                "젊음의 거리, 핫플집합소! 😉 (식당, 카페, PC방, 노래방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT,List.of(
                                                new PlaceRecommendResponse(2, 127.094741101863, 37.5351180385975,
                                                        "한우공방", "식당", 1,
                                                        "http://place.map.kakao.com/886708185"
                                                )
                                        ),
                                        RecommendCondition.CAFE,List.of(
                                                new PlaceRecommendResponse(1, 127.094741101863, 37.5351180385975,
                                                        "스타벅스 종로R점", "카페", 1,
                                                        "http://place.map.kakao.com/1784996243"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE,List.of(
                                                new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                        "옵티멈존 PC카페 종각역점", "PC방", 1,
                                                        "http://place.map.kakao.com/1342335656"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 2, 32, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "을지로3가",
                                                        126.991041, 37.566285, "2호선", 19),
                                                new PathResponse(2, "을지로3가", 126.991041, 37.566285, "을지로3가", 126.991041,
                                                        37.566285, null, 3),
                                                new PathResponse(3, "을지로3가", 126.991041, 37.566285, "종로3가", 126.991841,
                                                        37.571653, "3호선", 4),
                                                new PathResponse(4, "종로3가", 126.991841, 37.571653, "종로3가", 126.991841,
                                                        37.571653, null, 3),
                                                new PathResponse(5, "종로3가", 126.991841, 37.571653, "종각",
                                                        126.98315081716676, 37.570227990912244, "1호선", 3)
                                        ), List.of(
                                                new SubwayStationResponse(1, "강변역", 127.094741101863, 37.5351180385975),
                                                new SubwayStationResponse(2, "구의역", 127.086180837795, 37.5371752725594),
                                                new SubwayStationResponse(3, "건대입구역", 127.06920291650829, 37.54040751726388),
                                                new SubwayStationResponse(4, "성수역", 127.056066999327, 37.5445888153751),
                                                new SubwayStationResponse(5, "뚝섬역", 127.04738727881, 37.547241554679),
                                                new SubwayStationResponse(6, "한양대역", 127.043639802768, 37.5557159860408),
                                                new SubwayStationResponse(7, "왕십리역", 127.03710337610202, 37.561268363317176),
                                                new SubwayStationResponse(8, "상왕십리역", 127.02927241035283, 37.56443666620397),
                                                new SubwayStationResponse(9, "신당역", 127.019477533278, 37.5656730531732),
                                                new SubwayStationResponse(10, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802),
                                                new SubwayStationResponse(11, "을지로4가역", 126.997632059113, 37.5666405038268),
                                                new SubwayStationResponse(12, "을지로3가역", 126.99098443539428, 37.56629149790628),
                                                new SubwayStationResponse(13, "을지로입구역", 126.9821953112953, 37.566035517712955),
                                                new SubwayStationResponse(14, "시청역", 126.97719821079865, 37.56534539636417),
                                                new SubwayStationResponse(15, "종각역", 126.98315081716676, 37.570227990912244)
                                        )),
                                        new RouteResponse(2, 0, 5, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616, "종각",
                                                        126.98315081716676, 37.570227990912244, "1호선", 5)
                                        ), List.of(
                                                new SubwayStationResponse(1, "동대문역", 127.01063381083677, 37.571669405802616),
                                                new SubwayStationResponse(2, "종로5가역", 127.00153834521934, 37.57097610838373),
                                                new SubwayStationResponse(3, "종로3가역", 126.9921532525476, 37.570420844523),
                                                new SubwayStationResponse(4, "종각역", 126.98315081716676, 37.570227990912244)
                                        )),
                                        new RouteResponse(3, 2, 36, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 4),
                                                new PathResponse(2, "사당", 126.98155858357366, 37.47656223234824, "사당",
                                                        126.98155858357366, 37.47656223234824, null, 3),
                                                new PathResponse(3, "사당", 126.98155858357366, 37.47656223234824, "서울역",
                                                        126.972709, 37.553512, "4호선", 18),
                                                new PathResponse(4, "서울역", 126.972709, 37.553512, "서울역", 126.972709,
                                                        37.553512, null, 3),
                                                new PathResponse(5, "서울역", 126.972709, 37.553512, "종각",
                                                        126.98315081716676, 37.570227990912244, "1호선", 8)
                                        ), List.of(
                                                new SubwayStationResponse(1, "서울대입구역", 126.952713197762, 37.4812845080678),
                                                new SubwayStationResponse(2, "봉천역", 126.941686527151, 37.4824725161034),
                                                new SubwayStationResponse(3, "신림역", 126.9297453749671, 37.484267135140364),
                                                new SubwayStationResponse(4, "신대방역", 126.91350747615147, 37.48765046104574),
                                                new SubwayStationResponse(5, "구로디지털단지역", 126.901473080039, 37.4852605752505),
                                                new SubwayStationResponse(6, "대림역", 126.894931036051, 37.4933099444417),
                                                new SubwayStationResponse(7, "신도림역", 126.891312500851, 37.508908482648),
                                                new SubwayStationResponse(8, "문래역", 126.894778820701, 37.5179757181801),
                                                new SubwayStationResponse(9, "영등포구청역", 126.896677739939, 37.5258305311402),
                                                new SubwayStationResponse(10, "당산역", 126.902611795523, 37.5347843171332),
                                                new SubwayStationResponse(11, "합정역", 126.91445406513526, 37.54991315995173),
                                                new SubwayStationResponse(12, "홍대입구역", 126.923778562273, 37.5568707448873),
                                                new SubwayStationResponse(13, "신촌역", 126.93698075993808, 37.555198169366435),
                                                new SubwayStationResponse(14, "이대역", 126.94642954546576, 37.556814718869),
                                                new SubwayStationResponse(15, "아현역", 126.95614644008904, 37.55740617797663),
                                                new SubwayStationResponse(16, "충정로역", 126.9644920746172, 37.55976328822766),
                                                new SubwayStationResponse(17, "시청역", 126.97719821079865, 37.56534539636417),
                                                new SubwayStationResponse(18, "종각역", 126.98315081716676, 37.570227990912244)
                                        ))
                                )
                        ),
                        new RecommendationResponse(5L, 5, 37.5568707448873, 126.923778562273, "홍대입구역", 27, false,
                                "젊음의 거리, 놀거리 천국! 😎 (식당, 카페, PC방, 노래방, 오락시설)", "젊음과 문화의 거리인 홍대입구, 적절히 요소들을 잘 고려했습니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT,List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "하이디라오 홍대지점", "음식점", 1,
                                                        "http://place.map.kakao.com/1622865435")
                                        ),
                                        RecommendCondition.CAFE,List.of(
                                                new PlaceRecommendResponse(
                                                        2, 127.094741101863, 37.5351180385975,
                                                        "1984", "카페", 1,
                                                        "http://place.map.kakao.com/23634722"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE,List.of(
                                                new PlaceRecommendResponse(
                                                        3, 127.094741101863, 37.5351180385975,
                                                        "에스엔에스 피씨SNS PC", "PC방", 1,
                                                        "http://place.map.kakao.com/798252372"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 34, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 34)
                                        ), List.of(
                                                new SubwayStationResponse(1, "강변역", 127.094741101863, 37.5351180385975),
                                                new SubwayStationResponse(2, "구의역", 127.086180837795, 37.5371752725594),
                                                new SubwayStationResponse(3, "건대입구역", 127.06920291650829, 37.54040751726388),
                                                new SubwayStationResponse(4, "성수역", 127.056066999327, 37.5445888153751),
                                                new SubwayStationResponse(5, "뚝섬역", 127.04738727881, 37.547241554679),
                                                new SubwayStationResponse(6, "한양대역", 127.043639802768, 37.5557159860408),
                                                new SubwayStationResponse(7, "왕십리역", 127.03710337610202, 37.561268363317176),
                                                new SubwayStationResponse(8, "상왕십리역", 127.02927241035283, 37.56443666620397),
                                                new SubwayStationResponse(9, "신당역", 127.019477533278, 37.5656730531732),
                                                new SubwayStationResponse(10, "동대문역사문화공원역", 127.00900417014896, 37.56566440553802),
                                                new SubwayStationResponse(11, "을지로4가역", 126.997632059113, 37.5666405038268),
                                                new SubwayStationResponse(12, "을지로3가역", 126.99098443539428, 37.56629149790628),
                                                new SubwayStationResponse(13, "을지로입구역", 126.9821953112953, 37.566035517712955),
                                                new SubwayStationResponse(14, "시청역", 126.97719821079865, 37.56534539636417),
                                                new SubwayStationResponse(15, "충정로역", 126.9644920746172, 37.55976328822766),
                                                new SubwayStationResponse(16, "아현역", 126.95614644008904, 37.55740617797663),
                                                new SubwayStationResponse(17, "이대역", 126.94642954546576, 37.556814718869),
                                                new SubwayStationResponse(18, "신촌역", 126.93698075993808, 37.555198169366435),
                                                new SubwayStationResponse(19, "홍대입구역", 126.923778562273, 37.5568707448873)
                                        )),
                                        new RouteResponse(2, 1, 24, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 19)
                                        ), List.of(
                                                new SubwayStationResponse(1, "동대문역", 127.01063381083677, 37.571669405802616),
                                                new SubwayStationResponse(2, "종로5가역", 127.00153834521934, 37.57097610838373),
                                                new SubwayStationResponse(3, "종로3가역", 126.9921532525476, 37.570420844523),
                                                new SubwayStationResponse(4, "종각역", 126.98315081716676, 37.570227990912244),
                                                new SubwayStationResponse(5, "시청역", 126.97719821079865, 37.56534539636417),
                                                new SubwayStationResponse(6, "충정로역", 126.9644920746172, 37.55976328822766),
                                                new SubwayStationResponse(7, "아현역", 126.95614644008904, 37.55740617797663),
                                                new SubwayStationResponse(8, "이대역", 126.94642954546576, 37.556814718869),
                                                new SubwayStationResponse(9, "신촌역", 126.93698075993808, 37.555198169366435),
                                                new SubwayStationResponse(10, "홍대입구역", 126.923778562273, 37.5568707448873)
                                        )),
                                        new RouteResponse(3, 0, 25, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 25)
                                        ), List.of(
                                                new SubwayStationResponse(1, "서울대입구역", 126.952713197762, 37.4812845080678),
                                                new SubwayStationResponse(2, "봉천역", 126.941686527151, 37.4824725161034),
                                                new SubwayStationResponse(3, "신림역", 126.9297453749671, 37.484267135140364),
                                                new SubwayStationResponse(4, "신대방역", 126.91350747615147, 37.48765046104574),
                                                new SubwayStationResponse(5, "구로디지털단지역", 126.901473080039, 37.4852605752505),
                                                new SubwayStationResponse(6, "대림역", 126.894931036051, 37.4933099444417),
                                                new SubwayStationResponse(7, "신도림역", 126.891312500851, 37.508908482648),
                                                new SubwayStationResponse(8, "문래역", 126.894778820701, 37.5179757181801),
                                                new SubwayStationResponse(9, "영등포구청역", 126.896677739939, 37.5258305311402),
                                                new SubwayStationResponse(10, "당산역", 126.902611795523, 37.5347843171332),
                                                new SubwayStationResponse(11, "합정역", 126.91445406513526, 37.54991315995173),
                                                new SubwayStationResponse(12, "홍대입구역", 126.923778562273, 37.5568707448873)
                                        ))
                                )
                        )
                )
        );
    }

}
