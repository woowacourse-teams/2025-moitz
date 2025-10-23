package com.f12.moitz.infrastructure.client.kakao;

import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class KakaoMapConstants {

    public static final String SEARCH_PLACE_URL = "/local/search/keyword.json?query=%s&x=%s&y=%s&radius=%d";
    public static final String SEARCH_PLACE_WITH_SIZE_URL = "/local/search/keyword.json?query=%s&x=%s&y=%s&radius=%d&size=%d";
    public static final String SEARCH_POINT_URL = "/local/search/keyword.json?query=%s";
    public static final String SEARCH_IMAGE_URL = "/search/image?query=%s&page=%d&size=%d";

    public static final List<String> ERROR_CODE_CAN_RETRY = List.of("-1", "-7", "-603");
    public static final String ERROR_CODE_QUOTA_EXCEEDED = "-10";

    public static final int REQUEST_TIMEOUT_SECONDS = 5;
}
