package com.f12.moitz.infrastructure;

import com.f12.moitz.infrastructure.dto.KakaoApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class KakaoMapClient {

    private static final String KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local/search";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public String searchPlaceBy(final String placeName) {
        final String url = KAKAO_MAP_API_URL + "/address.json" + "?query=" + placeName;
        return getData(url).getPlaceName();
    }

    public String searchPlaceBy(final double longitude, final double latitude) {
        final String url = KAKAO_MAP_API_URL + "/category.json" + "?x=" + longitude + "&y=" + latitude + "&category_group_code=SW8";
        return getData(url).getPlaceName();
    }

    private KakaoApiResponse getData(final String url) {
        // TODO: 예외 처리, 응답에 따른 핸들링
        return restClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .body(KakaoApiResponse.class);
    }

}
