package com.f12.moitz.infrastructure.client.kakao.dto;

import com.f12.moitz.domain.RecommendCondition;
import java.util.List;
import java.util.Map;

public record KakaoApiResponses(
        Map<RecommendCondition, List<KakaoApiResponse>> kakaoApiResponses
) {

}
