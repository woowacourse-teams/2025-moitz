package com.f12.moitz.infrastructure.client.kakao.dto;

import java.util.List;

public record KakaoApiResponse(
        List<DocumentResponse> documents,
        MetaResponse meta
) {

    public double findStationX() {
        return Double.parseDouble(findStation().x());
    }

    public double findStationY() {
        return Double.parseDouble(findStation().y());
    }

    private DocumentResponse findStation() {
        return documents.stream()
                .filter(document -> "지하철역".equals(document.categoryGroupName()))
                .findFirst()
                .orElse(null);
    }

    public int size() {
        if (documents == null) {
            return 0;
        }
        return documents.size();
    }

}
