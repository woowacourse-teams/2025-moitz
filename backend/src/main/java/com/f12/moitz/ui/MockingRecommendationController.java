package com.f12.moitz.ui;

import com.f12.moitz.application.dto.LegacyRecommendationRequest;
import com.f12.moitz.application.dto.LegacyRecommendationResponse;
import com.f12.moitz.application.dto.LegacyRouteResponse;
import com.f12.moitz.application.dto.LocationResponse;
import com.f12.moitz.application.dto.MockLegacyRecommendationResponse;
import com.f12.moitz.application.dto.MockRecommendationResponse;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.PointResponse;
import com.f12.moitz.application.dto.RecommendationCreateResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import com.f12.moitz.domain.recommendation.RecommendCondition;
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
        return ResponseEntity.ok(new RecommendationCreateResponse("68F9860B4BB1192E031D93F8"));
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<MockRecommendationResponse> mockGetRecommendation(@PathVariable("id") String id) {
        return ResponseEntity.ok(mock());
    }

    private MockRecommendationResponse mock() {
        return new MockRecommendationResponse(
                List.of("식당", "카페", "PC방"),
                List.of(
                        new StartingPlaceResponse(1L, 1, 127.094741101863, 37.5351180385975, "강변역"),
                        new StartingPlaceResponse(2L, 2, 127.01063381083677, 37.571669405802616, "동대문역"),
                        new StartingPlaceResponse(3L, 3, 126.952713197762, 37.4812845080678, "서울대입구역")
                ),
                List.of(
                        new LocationResponse(1L, 1, 37.54040751726388, 127.06920291650829, "건대입구역", 18, false,
                                "GENERAL",
                                "다양한 즐길거리가 가득! 🥳 (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "육일관", "식당", 1,
                                                        "http://place.map.kakao.com/1050190897",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.CAFE, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "바나프레소 건대역점", "카페", 1,
                                                        "http://place.map.kakao.com/666587821",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "포포PC방 건대점", "PC방", 1,
                                                        "http://place.map.kakao.com/356960076",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 4, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975,
                                                        "건대입구", 127.06920291650829, 37.54040751726388, "2호선", 4)
                                        ), List.of(
                                                new PointResponse(1, 127.094741101863, 37.5351180385975),
                                                new PointResponse(2, 127.086180837795, 37.5371752725594),
                                                new PointResponse(3, 127.06920291650829, 37.54040751726388)
                                        )),
                                        new RouteResponse(2, 1, 18, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "건대입구",
                                                        127.06920291650829, 37.54040751726388, "2호선", 13)
                                        ), List.of(
                                                new PointResponse(1, 127.01063381083677, 37.571669405802616),
                                                new PointResponse(2, 127.00900417014896, 37.56566440553802),
                                                new PointResponse(3, 127.019477533278, 37.5656730531732),
                                                new PointResponse(4, 127.02927241035283, 37.56443666620397),
                                                new PointResponse(5, 127.03710337610202, 37.561268363317176),
                                                new PointResponse(6, 127.043639802768, 37.5557159860408),
                                                new PointResponse(7, 127.04738727881, 37.547241554679),
                                                new PointResponse(8, 127.056066999327, 37.5445888153751),
                                                new PointResponse(9, 127.06920291650829, 37.54040751726388)
                                        )),
                                        new RouteResponse(3, 0, 33, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "건대입구",
                                                        127.06920291650829, 37.54040751726388, "2호선", 33)
                                        ), List.of(
                                                new PointResponse(1, 126.952713197762, 37.4812845080678),
                                                new PointResponse(2, 126.963523905001, 37.4770932409965),
                                                new PointResponse(3, 126.98155858357366, 37.47656223234824),
                                                new PointResponse(4, 126.997553345516, 37.4814561268152),
                                                new PointResponse(5, 127.007662120039, 37.4918499338918),
                                                new PointResponse(6, 127.013867969161, 37.4927431676548),
                                                new PointResponse(7, 127.02800140627488, 37.49808633653005),
                                                new PointResponse(8, 127.03646946847, 37.5006744185994),
                                                new PointResponse(9, 127.04896282498558, 37.504497373023206),
                                                new PointResponse(10, 127.06302321147605, 37.508822740225305),
                                                new PointResponse(11, 127.073849447402, 37.5111446632705),
                                                new PointResponse(12, 127.086314327913, 37.5116263587296),
                                                new PointResponse(13, 127.10023101886318, 37.51331105877401),
                                                new PointResponse(14, 127.103808749487, 37.5207124124456),
                                                new PointResponse(15, 127.094741101863, 37.5351180385975),
                                                new PointResponse(16, 127.086180837795, 37.5371752725594),
                                                new PointResponse(17, 127.06920291650829, 37.54040751726388)
                                        ))
                                )
                        ),
                        new LocationResponse(2L, 2, 37.47656223234824, 126.98155858357366, "사당역", 18, false,
                                "GENERAL",
                                "만남의 광장, 맛집도 다양! 😋 (식당, 카페, PC방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "훈장골 사당점", "식당", 1,
                                                        "http://place.map.kakao.com/63778027",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.CAFE, List.of(
                                                new PlaceRecommendResponse(
                                                        2, 127.094741101863, 37.5351180385975,
                                                        "스타벅스 사당점", "카페", 1,
                                                        "http://place.map.kakao.com/23447734",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE, List.of(
                                                new PlaceRecommendResponse(
                                                        3, 127.094741101863, 37.5351180385975,
                                                        "레벨업PC방 사당역점", "PC방", 2,
                                                        "http://place.map.kakao.com/1705311839",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 25, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 25)
                                        ), List.of(
                                                new PointResponse(1, 127.094741101863, 37.5351180385975),
                                                new PointResponse(2, 127.103808749487, 37.5207124124456),
                                                new PointResponse(3, 127.10023101886318, 37.51331105877401),
                                                new PointResponse(4, 127.086314327913, 37.5116263587296),
                                                new PointResponse(5, 127.073849447402, 37.5111446632705),
                                                new PointResponse(6, 127.06302321147605, 37.508822740225305),
                                                new PointResponse(7, 127.04896282498558, 37.504497373023206),
                                                new PointResponse(8, 127.03646946847, 37.5006744185994),
                                                new PointResponse(9, 127.02800140627488, 37.49808633653005),
                                                new PointResponse(10, 127.013867961, 37.4927431676548),
                                                new PointResponse(11, 127.007662120039, 37.4918499338918),
                                                new PointResponse(12, 126.997553345516, 37.4814561268152),
                                                new PointResponse(13, 126.98155858357366, 37.47656223234824)
                                        )),
                                        new RouteResponse(2, 0, 25, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616, "사당",
                                                        126.98155858357366, 37.47656223234824, "4호선", 25)
                                        ), List.of(
                                                new PointResponse(1, 127.01063381083677, 37.571669405802616),
                                                new PointResponse(2, 127.00900417014896, 37.56566440553802),
                                                new PointResponse(3, 126.99414960395544, 37.56139658395457),
                                                new PointResponse(4, 126.98640235001736, 37.56096526943837),
                                                new PointResponse(5, 126.9784372569283, 37.55876114587941),
                                                new PointResponse(6, 126.96974961781686, 37.55332892758497),
                                                new PointResponse(7, 126.972118127373, 37.5445952301115),
                                                new PointResponse(8, 126.972922951307, 37.5344393447708),
                                                new PointResponse(9, 126.967965039183, 37.5292591489375),
                                                new PointResponse(10, 126.97354382085399, 37.52241291408466),
                                                new PointResponse(11, 126.97854611382496, 37.502744106282044),
                                                new PointResponse(12, 126.982211871752, 37.4867995957995),
                                                new PointResponse(13, 126.98155858357366, 37.47656223234824)
                                        )),
                                        new RouteResponse(3, 0, 4, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 4)
                                        ), List.of(
                                                new PointResponse(1, 126.952713197762, 37.4812845080678),
                                                new PointResponse(2, 126.963523905001, 37.4770932409965),
                                                new PointResponse(3, 126.98155858357366, 37.47656223234824)
                                        ))
                                )
                        ),
                        new LocationResponse(3L, 3, 37.561268363317176, 127.03710337610202, "왕십리역", 18, false,
                                "GENERAL",
                                "교통 요충지, 엔터-식사 해결! ✨ (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "스시도쿠", "식당", 1,
                                                        "http://place.map.kakao.com/26792732",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.CAFE, List.of(
                                                new PlaceRecommendResponse(
                                                        2, 127.094741101863, 37.5351180385975,
                                                        "탐앤탐스 왕십리역점", "카페", 1,
                                                        "http://place.map.kakao.com/10809505",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE, List.of(
                                                new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                        "이스포츠PC방 왕십리점", "PC방", 2,
                                                        "http://place.map.kakao.com/12326220",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 11, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "2호선", 11)
                                        ), List.of(
                                                new PointResponse(1, 127.094741101863, 37.5351180385975),
                                                new PointResponse(2, 127.086180837795, 37.5371752725594),
                                                new PointResponse(3, 127.06920291650829, 37.54040751726388),
                                                new PointResponse(4, 127.056066999327, 37.5445888153751),
                                                new PointResponse(5, 127.04738727881, 37.547241554679),
                                                new PointResponse(6, 127.043639802768, 37.5557159860408),
                                                new PointResponse(7, 127.03710337610202, 37.561268363317176),
                                                new PointResponse(8, 127.02927241035283, 37.56443666620397),
                                                new PointResponse(9, 127.019477533278, 37.5656730531732),
                                                new PointResponse(10, 127.00900417014896, 37.56566440553802)
                                        )),
                                        new RouteResponse(2, 1, 11, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "2호선", 6)
                                        ), List.of(
                                                new PointResponse(1, 127.01063381083677, 37.571669405802616),
                                                new PointResponse(2, 127.00900417014896, 37.56566440553802),
                                                new PointResponse(3, 127.00900417014896, 37.56566440553802),
                                                new PointResponse(4, 127.019477533278, 37.5656730531732),
                                                new PointResponse(5, 127.02927241035283, 37.56443666620397),
                                                new PointResponse(6, 127.03710337610202, 37.561268363317176)
                                        )),
                                        new RouteResponse(3, 1, 32, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "선릉",
                                                        127.049271, 37.504577, "2호선", 16),
                                                new PathResponse(2, "선릉", 127.049271, 37.504577, "선릉", 127.049271,
                                                        37.504577, null, 3),
                                                new PathResponse(3, "선릉", 127.049271, 37.504577, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "수인분당선", 13)
                                        ), List.of(
                                                new PointResponse(1, 126.952713197762, 37.4812845080678),
                                                new PointResponse(2, 126.963523905001, 37.4770932409965),
                                                new PointResponse(3, 126.98155858357366, 37.47656223234824),
                                                new PointResponse(4, 126.997553345516, 37.4814561268152),
                                                new PointResponse(5, 127.007662120039, 37.4918499338918),
                                                new PointResponse(6, 127.013867969161, 37.4927431676548),
                                                new PointResponse(7, 127.02800140627488, 37.49808633653005),
                                                new PointResponse(8, 127.03646946847, 37.5006744185994),
                                                new PointResponse(9, 127.04896282498558, 37.504497373023206),
                                                new PointResponse(10, 127.04896282498558, 37.504497373023206),
                                                new PointResponse(11, 127.043627289129, 37.5109326388803),
                                                new PointResponse(12, 127.0413109462156, 37.51721617197854),
                                                new PointResponse(13, 127.0406027693898, 37.5275184818021),
                                                new PointResponse(14, 127.044746216358, 37.543645796605),
                                                new PointResponse(15, 127.03710337610202, 37.561268363317176)
                                        ))
                                )
                        ),
                        new LocationResponse(4L, 4, 37.570227990912244, 126.98315081716676, "종각역", 24, false,
                                "GENERAL",
                                "젊음의 거리, 핫플집합소! 😉 (식당, 카페, PC방, 노래방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT, List.of(
                                                new PlaceRecommendResponse(2, 127.094741101863, 37.5351180385975,
                                                        "한우공방", "식당", 1,
                                                        "http://place.map.kakao.com/886708185",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.CAFE, List.of(
                                                new PlaceRecommendResponse(1, 127.094741101863, 37.5351180385975,
                                                        "스타벅스 종로R점", "카페", 1,
                                                        "http://place.map.kakao.com/1784996243",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE, List.of(
                                                new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                        "옵티멈존 PC카페 종각역점", "PC방", 1,
                                                        "http://place.map.kakao.com/1342335656",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
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
                                                new PointResponse(1, 127.094741101863, 37.5351180385975),
                                                new PointResponse(2, 127.086180837795, 37.5371752725594),
                                                new PointResponse(3, 127.06920291650829, 37.54040751726388),
                                                new PointResponse(4, 127.056066999327, 37.5445888153751),
                                                new PointResponse(5, 127.04738727881, 37.547241554679),
                                                new PointResponse(6, 127.043639802768, 37.5557159860408),
                                                new PointResponse(7, 127.03710337610202, 37.561268363317176),
                                                new PointResponse(8, 127.02927241035283, 37.56443666620397),
                                                new PointResponse(9, 127.019477533278, 37.5656730531732),
                                                new PointResponse(10, 127.00900417014896, 37.56566440553802),
                                                new PointResponse(11, 126.997632059113, 37.5666405038268),
                                                new PointResponse(12, 126.99098443539428, 37.56629149790628),
                                                new PointResponse(13, 126.9821953112953, 37.566035517712955),
                                                new PointResponse(14, 126.97719821079865, 37.56534539636417),
                                                new PointResponse(15, 126.98315081716676, 37.570227990912244)
                                        )),
                                        new RouteResponse(2, 0, 5, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616, "종각",
                                                        126.98315081716676, 37.570227990912244, "1호선", 5)
                                        ), List.of(
                                                new PointResponse(1, 127.01063381083677, 37.571669405802616),
                                                new PointResponse(2, 127.00153834521934, 37.57097610838373),
                                                new PointResponse(3, 126.9921532525476, 37.570420844523),
                                                new PointResponse(4, 126.98315081716676, 37.570227990912244)
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
                                                new PointResponse(1, 126.952713197762, 37.4812845080678),
                                                new PointResponse(2, 126.941686527151, 37.4824725161034),
                                                new PointResponse(3, 126.9297453749671, 37.484267135140364),
                                                new PointResponse(4, 126.91350747615147, 37.48765046104574),
                                                new PointResponse(5, 126.901473080039, 37.4852605752505),
                                                new PointResponse(6, 126.894931036051, 37.4933099444417),
                                                new PointResponse(7, 126.891312500851, 37.508908482648),
                                                new PointResponse(8, 126.894778820701, 37.5179757181801),
                                                new PointResponse(9, 126.896677739939, 37.5258305311402),
                                                new PointResponse(10, 126.902611795523, 37.5347843171332),
                                                new PointResponse(11, 126.91445406513526, 37.54991315995173),
                                                new PointResponse(12, 126.923778562273, 37.5568707448873),
                                                new PointResponse(13, 126.93698075993808, 37.555198169366435),
                                                new PointResponse(14, 126.94642954546576, 37.556814718869),
                                                new PointResponse(15, 126.95614644008904, 37.55740617797663),
                                                new PointResponse(16, 126.9644920746172, 37.55976328822766),
                                                new PointResponse(17, 126.97719821079865, 37.56534539636417),
                                                new PointResponse(18, 126.98315081716676, 37.570227990912244)
                                        ))
                                )
                        ),
                        new LocationResponse(5L, 5, 37.5568707448873, 126.923778562273, "홍대입구역", 27, false,
                                "GENERAL",
                                "젊음의 거리, 놀거리 천국! 😎 (식당, 카페, PC방, 노래방, 오락시설)", "젊음과 문화의 거리인 홍대입구, 적절히 요소들을 잘 고려했습니다.",
                                Map.of(
                                        RecommendCondition.RESTAURANT, List.of(
                                                new PlaceRecommendResponse(
                                                        1, 127.094741101863, 37.5351180385975,
                                                        "하이디라오 홍대지점", "음식점", 1,
                                                        "http://place.map.kakao.com/1622865435",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.CAFE, List.of(
                                                new PlaceRecommendResponse(
                                                        2, 127.094741101863, 37.5351180385975,
                                                        "1984", "카페", 1,
                                                        "http://place.map.kakao.com/23634722",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        ),
                                        RecommendCondition.PC_ROOM_KARAOKE, List.of(
                                                new PlaceRecommendResponse(
                                                        3, 127.094741101863, 37.5351180385975,
                                                        "에스엔에스 피씨SNS PC", "PC방", 1,
                                                        "http://place.map.kakao.com/798252372",
                                                        "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                                )
                                        )
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 34, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 34)
                                        ), List.of(
                                                new PointResponse(1, 127.094741101863, 37.5351180385975),
                                                new PointResponse(2, 127.086180837795, 37.5371752725594),
                                                new PointResponse(3, 127.06920291650829, 37.54040751726388),
                                                new PointResponse(4, 127.056066999327, 37.5445888153751),
                                                new PointResponse(5, 127.04738727881, 37.547241554679),
                                                new PointResponse(6, 127.043639802768, 37.5557159860408),
                                                new PointResponse(7, 127.03710337610202, 37.561268363317176),
                                                new PointResponse(8, 127.02927241035283, 37.56443666620397),
                                                new PointResponse(9, 127.019477533278, 37.5656730531732),
                                                new PointResponse(10, 127.00900417014896, 37.56566440553802),
                                                new PointResponse(11, 126.997632059113, 37.5666405038268),
                                                new PointResponse(12, 126.99098443539428, 37.56629149790628),
                                                new PointResponse(13, 126.9821953112953, 37.566035517712955),
                                                new PointResponse(14, 126.97719821079865, 37.56534539636417),
                                                new PointResponse(15, 126.9644920746172, 37.55976328822766),
                                                new PointResponse(16, 126.95614644008904, 37.55740617797663),
                                                new PointResponse(17, 126.94642954546576, 37.556814718869),
                                                new PointResponse(18, 126.93698075993808, 37.555198169366435),
                                                new PointResponse(19, 126.923778562273, 37.5568707448873)
                                        )),
                                        new RouteResponse(2, 1, 24, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 19)
                                        ), List.of(
                                                new PointResponse(1, 127.01063381083677, 37.571669405802616),
                                                new PointResponse(2, 127.00153834521934, 37.57097610838373),
                                                new PointResponse(3, 126.9921532525476, 37.570420844523),
                                                new PointResponse(4, 126.98315081716676, 37.570227990912244),
                                                new PointResponse(5, 126.97719821079865, 37.56534539636417),
                                                new PointResponse(6, 126.9644920746172, 37.55976328822766),
                                                new PointResponse(7, 126.95614644008904, 37.55740617797663),
                                                new PointResponse(8, 126.94642954546576, 37.556814718869),
                                                new PointResponse(9, 126.93698075993808, 37.555198169366435),
                                                new PointResponse(10, 126.923778562273, 37.5568707448873)
                                        )),
                                        new RouteResponse(3, 0, 25, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 25)
                                        ), List.of(
                                                new PointResponse(1, 126.952713197762, 37.4812845080678),
                                                new PointResponse(2, 126.941686527151, 37.4824725161034),
                                                new PointResponse(3, 126.9297453749671, 37.484267135140364),
                                                new PointResponse(4, 126.91350747615147, 37.48765046104574),
                                                new PointResponse(5, 126.901473080039, 37.4852605752505),
                                                new PointResponse(6, 126.894931036051, 37.4933099444417),
                                                new PointResponse(7, 126.891312500851, 37.508908482648),
                                                new PointResponse(8, 126.894778820701, 37.5179757181801),
                                                new PointResponse(9, 126.896677739939, 37.5258305311402),
                                                new PointResponse(10, 126.902611795523, 37.5347843171332),
                                                new PointResponse(11, 126.91445406513526, 37.54991315995173),
                                                new PointResponse(12, 126.923778562273, 37.5568707448873)
                                        ))
                                )
                        )
                )
        );
    }

    @PostMapping("/test/legacy")
    public ResponseEntity<RecommendationCreateResponse> mockRecommend(
            @RequestBody LegacyRecommendationRequest request) {
        return ResponseEntity.ok(new RecommendationCreateResponse("68F9860B4BB1192E031D93F8"));
    }

    @GetMapping("/test/legacy/{id}")
    public ResponseEntity<MockLegacyRecommendationResponse> mockLegacyGetRecommendation(@PathVariable("id") String id) {
        return ResponseEntity.ok(mockLegacy());
    }

    private MockLegacyRecommendationResponse mockLegacy() {
        return new MockLegacyRecommendationResponse(
                "CHAT",
                List.of(
                        new StartingPlaceResponse(1L, 1, 127.094741101863, 37.5351180385975, "강변역"),
                        new StartingPlaceResponse(2L, 2, 127.01063381083677, 37.571669405802616, "동대문역"),
                        new StartingPlaceResponse(3L, 3, 126.952713197762, 37.4812845080678, "서울대입구역")
                ),
                List.of(
                        new LegacyRecommendationResponse(1L, 1, 37.54040751726388, 127.06920291650829, "건대입구역", 18,
                                true,
                                "다양한 즐길거리가 가득! 🥳 (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, 127.094741101863, 37.5351180385975,
                                                "육일관", "식당", 1,
                                                "http://place.map.kakao.com/1050190897",
                                                "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                        ),
                                        new PlaceRecommendResponse(2, 127.094741101863, 37.5351180385975,
                                                "바나프레소 건대역점", "카페", 1,
                                                "http://place.map.kakao.com/666587821",
                                                "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                        ),
                                        new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                "포포PC방 건대점", "PC방", 1,
                                                "http://place.map.kakao.com/356960076",
                                                "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg")
                                ),
                                List.of(
                                        new LegacyRouteResponse(1, 0, 4, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "건대입구",
                                                        127.06920291650829
                                                        , 37.54040751726388
                                                        , "2호선", 4)
                                        )),
                                        new LegacyRouteResponse(2, 1, 18, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "건대입구",
                                                        127.06920291650829, 37.54040751726388, "2호선", 13)
                                        )),
                                        new LegacyRouteResponse(3, 0, 33, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "건대입구",
                                                        127.06920291650829, 37.54040751726388, "2호선", 33)
                                        ))
                                )
                        ),
                        new LegacyRecommendationResponse(2L, 2, 37.47656223234824, 126.98155858357366, "사당역", 18, true,
                                "만남의 광장, 맛집도 다양! 😋 (식당, 카페, PC방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                List.of(
                                        new PlaceRecommendResponse(
                                                1, 127.094741101863, 37.5351180385975,
                                                "훈장골 사당점", "식당", 1,
                                                "http://place.map.kakao.com/63778027",
                                                "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg"
                                        ),
                                        new PlaceRecommendResponse(
                                                2, 127.094741101863, 37.5351180385975,
                                                "스타벅스 사당점", "카페", 1,
                                                "http://place.map.kakao.com/23447734",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),
                                        new PlaceRecommendResponse(
                                                3, 127.094741101863, 37.5351180385975,
                                                "레벨업PC방 사당역점", "PC방", 2,
                                                "http://place.map.kakao.com/1705311839",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        )
                                ),
                                List.of(
                                        new LegacyRouteResponse(1, 0, 25, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 25)
                                        )),
                                        new LegacyRouteResponse(2, 0, 25, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616, "사당",
                                                        126.98155858357366, 37.47656223234824, "4호선", 25)
                                        )),
                                        new LegacyRouteResponse(3, 0, 4, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "사당",
                                                        126.98155858357366, 37.47656223234824, "2호선", 4)
                                        ))
                                )
                        ),
                        new LegacyRecommendationResponse(3L, 3, 37.561268363317176, 127.03710337610202, "왕십리역", 18,
                                true,
                                "교통 요충지, 엔터-식사 해결! ✨ (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                List.of(
                                        new PlaceRecommendResponse(
                                                1, 127.094741101863, 37.5351180385975,
                                                "스시도쿠", "식당", 1,
                                                "http://place.map.kakao.com/26792732",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),
                                        new PlaceRecommendResponse(
                                                2, 127.094741101863, 37.5351180385975,
                                                "탐앤탐스 왕십리역점", "카페", 1,
                                                "http://place.map.kakao.com/10809505",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),
                                        new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                "이스포츠PC방 왕십리점", "PC방", 2,
                                                "http://place.map.kakao.com/12326220",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        )
                                ),
                                List.of(
                                        new LegacyRouteResponse(1, 0, 11, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "2호선", 11)
                                        )),
                                        new LegacyRouteResponse(2, 1, 11, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "2호선", 6)
                                        )),
                                        new LegacyRouteResponse(3, 1, 32, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "선릉",
                                                        127.049271, 37.504577, "2호선", 16),
                                                new PathResponse(2, "선릉", 127.049271, 37.504577, "선릉", 127.049271,
                                                        37.504577, null, 3),
                                                new PathResponse(3, "선릉", 127.049271, 37.504577, "왕십리",
                                                        127.03710337610202, 37.561268363317176, "수인분당선", 13)
                                        ))
                                )
                        ),
                        new LegacyRecommendationResponse(4L, 4, 37.570227990912244, 126.98315081716676, "종각역", 24,
                                false,
                                "젊음의 거리, 핫플집합소! 😉 (식당, 카페, PC방, 노래방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, 127.094741101863, 37.5351180385975,
                                                "스타벅스 종로R점", "카페", 1,
                                                "http://place.map.kakao.com/1784996243",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),
                                        new PlaceRecommendResponse(2, 127.094741101863, 37.5351180385975,
                                                "한우공방", "식당", 1,
                                                "http://place.map.kakao.com/886708185",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),
                                        new PlaceRecommendResponse(3, 127.094741101863, 37.5351180385975,
                                                "옵티멈존 PC카페 종각역점", "PC방", 1,
                                                "http://place.map.kakao.com/1342335656",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        )
                                ),
                                List.of(
                                        new LegacyRouteResponse(1, 2, 32, List.of(
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
                                        )),
                                        new LegacyRouteResponse(2, 0, 5, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616, "종각",
                                                        126.98315081716676, 37.570227990912244, "1호선", 5)
                                        )),
                                        new LegacyRouteResponse(3, 2, 36, List.of(
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
                                        ))
                                )
                        ),
                        new LegacyRecommendationResponse(5L, 5, 37.5568707448873, 126.923778562273, "홍대입구역", 27, false,
                                "젊음의 거리, 놀거리 천국! 😎 (식당, 카페, PC방, 노래방, 오락시설)", "젊음과 문화의 거리인 홍대입구, 적절히 요소들을 잘 고려했습니다.",
                                List.of(
                                        new PlaceRecommendResponse(
                                                1, 127.094741101863, 37.5351180385975,
                                                "하이디라오 홍대지점", "음식점", 1,
                                                "http://place.map.kakao.com/1622865435",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),

                                        new PlaceRecommendResponse(
                                                2, 127.094741101863, 37.5351180385975,
                                                "1984", "카페", 1,
                                                "http://place.map.kakao.com/23634722",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        ),
                                        new PlaceRecommendResponse(
                                                3, 127.094741101863, 37.5351180385975,
                                                "에스엔에스 피씨SNS PC", "PC방", 1,
                                                "http://place.map.kakao.com/798252372",
                                                "https://postfiles.pstatic.net/MjAyNTEwMTFfMTc4/MDAxNzYwMTc5OTY1Nzk0.g0QMYYmtwApndSwRP589w3xB4FXUx4hoYJqM2WC16MIg.N6yAEBTJDWquseEPWtc5DLTZSjN__3msUVDXUJobF-gg.PNG/image.png?type=w966"
                                        )
                                ),
                                List.of(
                                        new LegacyRouteResponse(1, 0, 34, List.of(
                                                new PathResponse(1, "강변", 127.094741101863, 37.5351180385975, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 34)
                                        )),
                                        new LegacyRouteResponse(2, 1, 24, List.of(
                                                new PathResponse(1, "동대문", 127.01063381083677, 37.571669405802616,
                                                        "동대문역사문화공원", 127.007821, 37.565147, "4호선", 2),
                                                new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147, "동대문역사문화공원",
                                                        127.007821, 37.565147, null, 3),
                                                new PathResponse(3, "동대문역사문화공원", 127.007821, 37.565147, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 19)
                                        )),
                                        new LegacyRouteResponse(3, 0, 25, List.of(
                                                new PathResponse(1, "서울대입구", 126.952713197762, 37.4812845080678, "홍대입구",
                                                        126.923778562273, 37.5568707448873, "2호선", 25)
                                        ))
                                )
                        )
                )
        );
    }

}
