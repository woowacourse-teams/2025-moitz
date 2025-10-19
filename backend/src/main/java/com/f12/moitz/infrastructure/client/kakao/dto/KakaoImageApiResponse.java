package com.f12.moitz.infrastructure.client.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record KakaoImageApiResponse(
        List<ImageDocumentResponse> documents,
        MetaResponse meta
) {

    public record ImageDocumentResponse(
            String collection,
            @JsonProperty("thumbnail_url") String thumbnailUrl,
            @JsonProperty("image_url") String imageUrl,
            Integer width,
            Integer height,
            @JsonProperty("display_sitename") String displaySitename,
            @JsonProperty("doc_url") String docUrl,
            String datetime
    ) {

    }

}