package com.f12.moitz.infrastructure.client.kakao.dto;

import java.util.List;
import java.util.Map;

public record KakaoApiResponses(
        Map<String, List<KakaoApiResponse>> kakaoApiResponses
) {

}
