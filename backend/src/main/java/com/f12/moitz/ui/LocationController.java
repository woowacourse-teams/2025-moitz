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
                                126.924397990207,
                                37.5217753947299,
                                "여의도역"
                        ),
                        new StartingPlaceResponse(
                                2L,
                                2,
                                127.132396300314,
                                37.5358819145235,
                                "강동역"
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
                                1L, 1, 37.47656223234824, 126.98155858357366, "사당역", 20, true,
                                "다양한 편의시설과 맛집! 😋", "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, "훈장골 사당점", "식당", 1, "http://place.map.kakao.com/63778027"),
                                        new PlaceRecommendResponse(2, "스타벅스 사당점", "카페", 1, "http://place.map.kakao.com/23447734"),
                                        new PlaceRecommendResponse(3, "레벨업PC방 사당역점", "PC방", 2, "http://place.map.kakao.com/1705311839")
                                ),
                                List.of(
                                        new RouteResponse(1, 1, 20, List.of(
                                                new PathResponse(1, "여의도", 126.924024, 37.521759, "동작", 126.977765, 37.503125, "9호선", 7),
                                                new PathResponse(2, "동작", 126.977765, 37.503125, "동작", 126.977765, 37.503125, null, 3),
                                                new PathResponse(3, "동작", 126.980335, 37.502913, "사당", 126.981662, 37.476793, "4호선", 10)
                                        )),
                                        new RouteResponse(2, 2, 38, List.of(
                                                new PathResponse(1, "강동", 127.1323, 37.535905, "천호", 127.123896, 37.538483, "5호선", 2),
                                                new PathResponse(2, "천호", 127.123896, 37.538483, "천호", 127.123896, 37.538483, null, 3),
                                                new PathResponse(3, "천호", 127.123246, 37.53801, "잠실", 127.10466, 37.514768, "8호선", 7),
                                                new PathResponse(4, "잠실", 127.10466, 37.514768, "잠실", 127.10466, 37.514768, null, 3),
                                                new PathResponse(5, "잠실", 127.100164, 37.513346, "사당", 126.981359, 37.476575, "2호선", 23)
                                        )),
                                        new RouteResponse(3, 0, 4, List.of(
                                                new PathResponse(1, "서울대입구", 126.952725, 37.481199, "사당", 126.981359, 37.476575, "2호선", 4)
                                        ))
                                )
                        ),
                        new RecommendationResponse(
                                2L, 2, 37.484267135140364, 126.9297453749671, "신림역", 23, false,
                                "저렴하고 다양한 즐길 거리! 🎲", "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, "원조민속순대타운", "음식점", 2, "http://place.map.kakao.com/15317417"),
                                        new PlaceRecommendResponse(2, "폼앤노말", "카페", 3, "http://place.map.kakao.com/1168065433"),
                                        new PlaceRecommendResponse(3, "하이브PC방", "PC방", 1, "http://place.map.kakao.com/1082548832")
                                ),
                                List.of(
                                        new RouteResponse(1, 1, 19, List.of(
                                                new PathResponse(1, "여의도", 126.924024, 37.521759, "샛강", 126.928881, 37.516739, "9호선", 2),
                                                new PathResponse(2, "샛강", 126.928881, 37.516739, "샛강", 126.928881, 37.516739, null, 3),
                                                new PathResponse(3, "샛강", 126.929282, 37.51713, "신림", 126.929643, 37.484732, "신림선", 14)
                                        )),
                                        new RouteResponse(2, 2, 46, List.of(
                                                new PathResponse(1, "강동", 127.1323, 37.535905, "천호", 127.123896, 37.538483, "5호선", 2),
                                                new PathResponse(2, "천호", 127.123896, 37.538483, "천호", 127.123896, 37.538483, null, 3),
                                                new PathResponse(3, "천호", 127.123246, 37.53801, "잠실", 127.10466, 37.514768, "8호선", 7),
                                                new PathResponse(4, "잠실", 127.10466, 37.514768, "잠실", 127.10466, 37.514768, null, 3),
                                                new PathResponse(5, "잠실", 127.100164, 37.513346, "신림", 126.929695, 37.484228, "2호선", 31)
                                        )),
                                        new RouteResponse(3, 0, 4, List.of(
                                                new PathResponse(1, "서울대입구", 126.952725, 37.481199, "신림", 126.929695, 37.484228, "2호선", 4)
                                        ))
                                )
                        ),
                        new RecommendationResponse(
                                3L, 3, 37.561268363317176, 127.03710337610202, "왕십리역", 25, false,
                                "교통 요충지, 복합 쇼핑몰! 🛍️", "다양한 장소에서 접근하기 편하며, 여러 만남 장소가 존재하는 최적의 장소 중 하나입니다.️",
                                List.of(
                                        new PlaceRecommendResponse(1, "스시도쿠", "식당", 1, "http://place.map.kakao.com/26792732"),
                                        new PlaceRecommendResponse(2, "탐앤탐스 왕십리역점", "카페", 1, "http://place.map.kakao.com/10809505"),
                                        new PlaceRecommendResponse(3, "이스포츠PC방 왕십리점", "PC방", 2, "http://place.map.kakao.com/12326220")
                                ),
                                List.of(
                                        new RouteResponse(1, 0, 26, List.of(
                                                new PathResponse(1, "여의도", 126.924071, 37.521624, "왕십리", 127.037226, 37.56184, "5호선", 26)
                                        )),
                                        new RouteResponse(2, 0, 17, List.of(
                                                new PathResponse(1, "강동", 127.1323, 37.535905, "왕십리", 127.037226, 37.56184, "5호선", 17)
                                        )),
                                        new RouteResponse(3, 1, 32, List.of(
                                                new PathResponse(1, "서울대입구", 126.952725, 37.481199, "선릉", 127.049271, 37.504577, "2호선", 16),
                                                new PathResponse(2, "선릉", 127.049271, 37.504577, "선릉", 127.049271, 37.504577, null, 3),
                                                new PathResponse(3, "선릉", 127.048606, 37.505274, "왕십리", 127.038702, 37.561501, "수인분당선", 13)
                                        ))
                                )
                        ),
                        new RecommendationResponse(
                                4L, 4, 37.54040751726388, 127.06920291650829, "건대입구역", 28, false,
                                "젊음의 거리, 놀거리 가득! 🥳", "어디에서 출발해도 교통이 좋은 중심지이며, 다양한 만남 장소가 존재하는 최적의 장소 중 하나입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, "송화양꼬치", "식당", 4, "http://place.map.kakao.com/14822911"),
                                        new PlaceRecommendResponse(2, "도우터", "카페", 3, "http://place.map.kakao.com/1926044933"),
                                        new PlaceRecommendResponse(3, "포포PC방 건대점", "PC방", 1, "http://place.map.kakao.com/356960076")
                                ),
                                List.of(
                                        new RouteResponse(1, 2, 35, List.of(
                                                new PathResponse(1, "여의도", 126.924024, 37.521759, "선정릉", 127.043409, 37.510134, "9호선", 16),
                                                new PathResponse(2, "선정릉", 127.043409, 37.510134, "선정릉", 127.043409, 37.510134, null, 3),
                                                new PathResponse(3, "선정릉", 127.043637, 37.51092, "강남구청", 127.041401, 37.51684, "수인분당선", 4),
                                                new PathResponse(4, "강남구청", 127.041401, 37.51684, "강남구청", 127.041401, 37.51684, null, 3),
                                                new PathResponse(5, "강남구청", 127.041283, 37.517181, "건대입구", 127.071092, 37.540865, "7호선", 9)
                                        )),
                                        new RouteResponse(2, 1, 17, List.of(
                                                new PathResponse(1, "강동", 127.1323, 37.535905, "군자", 127.079412, 37.557185, "5호선", 9),
                                                new PathResponse(2, "군자", 127.079412, 37.557185, "군자", 127.079412, 37.557185, null, 3),
                                                new PathResponse(3, "군자", 127.079536, 37.557222, "건대입구", 127.071092, 37.540865, "7호선", 5)
                                        )),
                                        new RouteResponse(3, 0, 33, List.of(
                                                new PathResponse(1, "서울대입구", 126.952725, 37.481199, "건대입구", 126.929695, 37.484228, "2호선", 33)
                                        ))
                                )
                        ),
                        new RecommendationResponse(
                                5L, 5, 37.5568707448873, 126.923778562273, "홍대입구역", 28, false,
                                "맛집, 카페, 놀거리 천국! 🎨", "다양한 장소에서 접근하기 편하며, 주어진 카테고리에 대한 요건을 만족하는 가장 추천드리는 선택지입니다.",
                                List.of(
                                        new PlaceRecommendResponse(1, "하이디라오 홍대지점", "식당", 1, "http://place.map.kakao.com/1622865435"),
                                        new PlaceRecommendResponse(2, "1984", "카페", 1, "http://place.map.kakao.com/23634722"),
                                        new PlaceRecommendResponse(3, "에스엔에스 피씨SNS PC", "PC방", 1, "http://place.map.kakao.com/798252372")
                                ),
                                List.of(
                                        new RouteResponse(1, 1, 15, List.of(
                                                new PathResponse(1, "여의도", 126.924024, 37.521759, "당산", 126.90264, 37.533537, "9호선", 3),
                                                new PathResponse(2, "당산", 126.90264, 37.533537, "당산", 126.90264, 37.533537, null, 3),
                                                new PathResponse(3, "당산", 126.902677, 37.534871, "홍대입구", 126.924016, 37.557008, "2호선", 9)
                                        )),
                                        new RouteResponse(2, 1, 45, List.of(
                                                new PathResponse(1, "강동", 127.1323, 37.535905, "왕십리", 127.037226, 37.56184, "5호선", 17),
                                                new PathResponse(2, "왕십리", 127.037226, 37.56184, "왕십리", 127.037226, 37.56184, null, 3),
                                                new PathResponse(3, "왕십리", 127.037245, 37.561219, "홍대입구", 126.924016, 37.557008, "2호선", 25)
                                        )),
                                        new RouteResponse(3, 0, 25, List.of(
                                                new PathResponse(1, "서울대입구", 126.952725, 37.481199, "홍대입구", 126.924016, 37.557008, "2호선", 25)
                                        ))
                                )
                        )
                )
        );
    }

}
