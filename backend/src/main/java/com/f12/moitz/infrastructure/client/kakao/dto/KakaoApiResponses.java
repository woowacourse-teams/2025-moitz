package com.f12.moitz.infrastructure.client.kakao.dto;

import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoApiResponses {

    private final Map<String, List<KakaoApiResponse>> kakaoApiResponses;

    public KakaoApiResponses(Map<String, List<KakaoApiResponse>> kakaoApiResponses) {
        this.kakaoApiResponses = kakaoApiResponses;
    }
}
