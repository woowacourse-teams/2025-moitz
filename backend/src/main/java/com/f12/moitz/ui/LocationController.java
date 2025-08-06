package com.f12.moitz.ui;

import com.f12.moitz.application.RecommendationService;
import com.f12.moitz.application.dto.PathResponse;
import com.f12.moitz.application.dto.PlaceRecommendResponse;
import com.f12.moitz.application.dto.RecommendationRequest;
import com.f12.moitz.application.dto.RecommendationResponse;
import com.f12.moitz.application.dto.RecommendationsResponse;
import com.f12.moitz.application.dto.RouteResponse;
import com.f12.moitz.application.dto.StartingPlaceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/locations")
public class LocationController implements SwaggerLocationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationsResponse> recommendLocations(@RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(recommendationService.recommendLocation(request));
    }

    @PostMapping("/test")
    public ResponseEntity<RecommendationsResponse> mockRecommendedLocation(@RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(mock());
    }

    private RecommendationsResponse mock() {
        return new RecommendationsResponse(
                List.of(
                        new StartingPlaceResponse(
                                1L,
                                1,
                                127.094741101863,
                                37.5351180385975,
                                "강변역"
                        ),
                        new StartingPlaceResponse(
                                2L,
                                2,
                                127.01063381083677,
                                37.571669405802616,
                                "동대문역"
                        ),
                        new StartingPlaceResponse(
                                3L,
                                3,
                                126.952713197762,
                                37.4812845080678,
                                "서울대입구역"
                        )
                ),
                List.of(
                        new RecommendationResponse(
                                1L,
                                1,
                                37.47656223234824,
                                126.98155858357366,
                                "사당역",
                                18,
                                true,
                                "만남의 광장, 맛집 천국! 😋 (식당, 카페, PC방)",
                                "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                List.of(
                                        new PlaceRecommendResponse(2, "훈장골 사당점", "음식점", 1,
                                                "http://place.map.kakao.com/63778027"),
                                        new PlaceRecommendResponse(2, "스타벅스 사당점", "카페", 1,
                                                "http://place.map.kakao.com/23447734"),
                                        new PlaceRecommendResponse(2, "레벨업PC방 사당역점", "PC방", 2,
                                                "http://place.map.kakao.com/1705311839")
                                ),
                                List.of(
                                        new RouteResponse(
                                                1, 0, 25,
                                                List.of(new PathResponse(1, "강변", 127.094687, 37.535092, "사당",
                                                        126.981359, 37.476575, "2호선", 25))
                                        ),
                                        new RouteResponse(
                                                2, 0, 4,
                                                List.of(new PathResponse(1, "서울대입구", 126.952725, 37.481199, "사당",
                                                        126.981359, 37.476575, "2호선", 4))
                                        ),
                                        new RouteResponse(
                                                3, 0, 25,
                                                List.of(new PathResponse(1, "동대문", 127.009111, 37.571132, "사당",
                                                        126.981662, 37.476793, "4호선", 25))
                                        )
                                )
                        ),
                        new RecommendationResponse(
                                2L,
                                2,
                                37.49808633653005,
                                127.02800140627488,
                                "강남역",
                                19,
                                false,
                                "핫플레이스의 중심! ✨ (식당, 카페, PC방, 노래방, 오락시설)",
                                "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, "감성타코 강남역점", "음식점", 1,
                                                "http://place.map.kakao.com/1465968863"),
                                        new PlaceRecommendResponse(2, "오레노라멘 강남점", "음식점", 3,
                                                "http://place.map.kakao.com/1950857675"),
                                        new PlaceRecommendResponse(3, "스타벅스 강남R점", "카페", 1,
                                                "http://place.map.kakao.com/35026031")
                                ),
                                List.of(
                                        new RouteResponse(
                                                1, 0, 16,
                                                List.of(new PathResponse(1, "강변", 127.094687, 37.535092, "강남",
                                                        127.027618, 37.497949, "2호선", 16))
                                        ),
                                        new RouteResponse(
                                                2, 0, 13,
                                                List.of(new PathResponse(1, "서울대입구", 126.952725, 37.481199, "강남",
                                                        127.027618, 37.497949, "2호선", 13))
                                        ),
                                        new RouteResponse(
                                                3, 2, 30,
                                                List.of(
                                                        new PathResponse(1, "동대문", 127.009111, 37.571132, "충무로",
                                                                126.994199, 37.561266, "4호선", 4),
                                                        new PathResponse(2, "충무로", 126.994199, 37.561266, "충무로",
                                                                126.994199, 37.561266, null, 3),
                                                        new PathResponse(3, "충무로", 126.994723, 37.560991, "신사",
                                                                127.020399, 37.516479, "3호선", 13),
                                                        new PathResponse(4, "신사", 127.020399, 37.516479, "신사",
                                                                127.020399, 37.516479, null, 3),
                                                        new PathResponse(5, "신사", 127.019568, 37.516012, "강남",
                                                                127.028351, 37.49637, "신분당", 7)
                                                )
                                        )
                                )
                        ),
                        new RecommendationResponse(
                                3L,
                                3,
                                37.5568707448873,
                                126.923778562273,
                                "홍대입구역",
                                27,
                                false,
                                "젊음과 문화의 거리! 😎 (식당, 카페, PC방, 노래방, 오락시설)",
                                "젊음과 문화의 거리인 홍대입구, 적절히 요소들을 잘 고려했습니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, "하이디라오 홍대지점", "음식점", 1,
                                                "http://place.map.kakao.com/1622865435"),
                                        new PlaceRecommendResponse(2, "1984", "카페", 1,
                                                "http://place.map.kakao.com/23634722"),
                                        new PlaceRecommendResponse(3, "에스엔에스 피씨SNS PC", "PC방", 1,
                                                "http://place.map.kakao.com/798252372")
                                ),
                                List.of(
                                        new RouteResponse(
                                                1, 0, 34,
                                                List.of(new PathResponse(1, "강변", 127.094687, 37.535092, "홍대입구",
                                                        126.924016, 37.557008, "2호선", 34))
                                        ),
                                        new RouteResponse(
                                                2, 0, 25,
                                                List.of(new PathResponse(1, "서울대입구", 126.952725, 37.481199, "홍대입구",
                                                        126.924016, 37.557008, "2호선", 25))
                                        ),
                                        new RouteResponse(
                                                3, 1, 24,
                                                List.of(
                                                        new PathResponse(1, "동대문", 127.009111, 37.571132, "동대문역사문화공원",
                                                                127.007821, 37.565147, "4호선", 2),
                                                        new PathResponse(2, "동대문역사문화공원", 127.007821, 37.565147,
                                                                "동대문역사문화공원", 127.007821, 37.565147, null, 3),
                                                        new PathResponse(3, "동대문역사문화공원", 127.008912, 37.56567, "홍대입구",
                                                                126.924016, 37.557008, "2호선", 19)
                                                )
                                        )
                                )
                        )
                )
        );
    }

}
