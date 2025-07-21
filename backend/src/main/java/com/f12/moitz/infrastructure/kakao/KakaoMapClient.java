package com.f12.moitz.infrastructure.kakao;

import com.f12.moitz.domain.Point;
import com.f12.moitz.infrastructure.kakao.dto.KakaoApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class KakaoMapClient {

    private static final String KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local/search";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public Point searchPointBy(final String placeName) {
        final String url = KAKAO_MAP_API_URL + "/keyword.json" + "?query=" + placeName;
        final KakaoApiResponse response = getData(url);
        return new Point(response.getX(), response.getY());
    }

    public String searchPlaceBy(final double longitude, final double latitude) {
        final String url = KAKAO_MAP_API_URL + "/category.json" + "?x=" + longitude + "&y=" + latitude + "&category_group_code=SW8";
        final KakaoApiResponse response = getData(url);
        return response.getPlaceName();
    }

    private KakaoApiResponse getData(final String url) {
        // TODO: 예외 처리, 응답에 따른 핸들링
        final KakaoApiResponse response = restClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .body(KakaoApiResponse.class);

        log.info("카카오맵 장소 조회 API 응답 성공 : {}", response.getPlaceName());

        return response;
    }

}
