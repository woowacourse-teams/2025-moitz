package com.f12.moitz.application.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.recommendation.RecommendCondition;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "지역 추천 응답")
public record LocationResponse(
        @Schema(description = "ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "순번", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        int index,
        @Schema(description = "위도", example = "37.49808633653005", requiredMode = Schema.RequiredMode.REQUIRED)
        double y,
        @Schema(description = "경도", example = "127.02800140627488", requiredMode = Schema.RequiredMode.REQUIRED)
        double x,
        @Schema(description = "추천 지역 이름", example = "강남역", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "평균 이동 시간", example = "21", requiredMode = Schema.RequiredMode.REQUIRED)
        int avgMinutes,
        @Schema(description = "최적의 추천 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean isBest,
        @Schema(description = "추천 태그 코드", example = "FAIRNESS", requiredMode = Schema.RequiredMode.REQUIRED)
        String tag,
        @Schema(description = "추천 태그 설명", example = "환승 부담이 적은 기준", requiredMode = Schema.RequiredMode.REQUIRED)
        String tagInfo,
        @Schema(description = "추천 이유 해시태그", example = "#최소환승", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @Schema(description = "지역 추천 이유 문장", example = "서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        String reason,
        @Schema(description = "지역 추천 정보", example = "서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        String locationInfo,
        @Schema(
                description = "카테고리별 추천 장소 목록",
                example = """
                        {
                          "PC_ROOM_KARAOKE": [
                            {
                              "index": 1,
                              "x": 127.007079969366,
                              "y": 37.5657600421876,
                              "name": "GGX",
                              "category": "게임방,PC방",
                              "walkingTime": 3,
                              "placeUrl": "http://place.map.kakao.com/1381860160",
                              "imageUrl": "https://postfiles.pstatic.net/MjAyMTEwMjhfMTEw/MDAxNjM1Mzc3NjcwMzA5.mlzjuMBP4_ecbkV2cWnn7-a9RyP6CAGLiRR5uXQ37BUg.pNE9lcKIZjDjYngjT2UIf9Hs6yqhD9dE421BhcleDGgg.JPEG.sodaezzang_/IMG_3701.jpg?type=w966"
                            },
                            {
                              "index": 2,
                              "x": 127.006320492392,
                              "y": 37.5660078592087,
                              "name": "캐슬노래연습장",
                              "category": "노래방",
                              "walkingTime": 4,
                              "placeUrl": "http://place.map.kakao.com/324019013",
                              "imageUrl": "https://postfiles.pstatic.net/MjAyNDAyMDRfMjI2/MDAxNzA3MDM1MDQxMjkw.TM-AlORoiO3cu30rGCJUEpBasRDtR7RJcwqYM7fHTP8g.imv5ph_iCCJpOVGfICx1JNObKXIXDJ_Xb94E_Tp5Cuwg.PNG.cnrqhrdhfps/image.png?type=w966"
                            }
                          ],
                          "RESTAURANT": [
                            {
                              "index": 1,
                              "x": 127.00669603395768,
                              "y": 37.56324808702588,
                              "name": "평양면옥 본점",
                              "category": "냉면",
                              "walkingTime": 5,
                              "placeUrl": "http://place.map.kakao.com/13093208",
                              "imageUrl": "https://postfiles.pstatic.net/MjAyNTA2MTFfMjE4/MDAxNzQ5NjE4MTYwMTAz.4S-VRTx8pxts6OH157TlWJQYyVPuLiZRyQurIMbxrBog.K38nx_svd1lCq-KK6KMNYglno5SBfMNfL_56iLHkxxMg.PNG/IMG%EF%BC%BF4634.PNG?type=w275"
                            }
                          ],
                          "CAFE": [
                            {
                              "index": 1,
                              "x": 127.007230456427,
                              "y": 37.5651987124977,
                              "name": "스타벅스 동대문공원점",
                              "category": "카페",
                              "walkingTime": 2,
                              "placeUrl": "http://place.map.kakao.com/321907882",
                              "imageUrl": "https://postfiles.pstatic.net/MjAyMDA1MTJfMzQg/MDAxNTg5MjczNTg3MDg2.nIgiLGyvbPxicWmTqyrpvhh4-JGeMHbD8DyKtfs1CAUg.N0cNeX5A_BpjwvdXVDrBxBYKinMZekyWXpwrQqHEeHMg.JPEG.kksy062/1589273588932.jpg?type=w773"
                            }
                          ]
                        }
                        """
        )
        Map<RecommendCondition, List<PlaceRecommendResponse>> places,
        @Schema(description = "각 출발지로부터 이동 경로", requiredMode = Schema.RequiredMode.REQUIRED)
        List<RouteResponse> routes
) {

    public LocationResponse {
        tag = validateTag(tag);
    }

    public LocationResponse(
            final Long id,
            final int index,
            final double y,
            final double x,
            final String name,
            final int avgMinutes,
            final boolean isBest,
            final String tag,
            final String description,
            final String reason,
            final Map<RecommendCondition, List<PlaceRecommendResponse>> places,
            final List<RouteResponse> routes
    ) {
        this(
                id,
                index,
                y,
                x,
                name,
                avgMinutes,
                isBest,
                validateTag(tag),
                resolveTagInfo(tag),
                description,
                reason,
                reason,
                places,
                routes
        );
    }

    public LocationResponse(
            final Long id,
            final int index,
            final double y,
            final double x,
            final String name,
            final int avgMinutes,
            final boolean isBest,
            final List<String> tags,
            final String description,
            final String reason,
            final Map<RecommendCondition, List<PlaceRecommendResponse>> places,
            final List<RouteResponse> routes
    ) {
        this(
                id,
                index,
                y,
                x,
                name,
                avgMinutes,
                isBest,
                validateTags(tags).getFirst(),
                resolveTagInfo(tags),
                description,
                reason,
                reason,
                places,
                routes
        );
    }

    private static String validateTag(final String tag) {
        if (tag == null || tag.isBlank()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG, tag);
        }
        return tag;
    }

    private static List<String> validateTags(final List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG, tags);
        }
        if (tags.size() != 1) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG, tags);
        }
        return tags.stream()
                .map(LocationResponse::validateTag)
                .toList();
    }

    private static String resolveTagInfo(final List<String> tags) {
        return resolveTagInfo(validateTags(tags).getFirst());
    }

    private static String resolveTagInfo(final String tag) {
        final String validatedTag = validateTag(tag);
        try {
            return CandidateSelectionTag.valueOf(validatedTag).getDescription();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG, e, validatedTag);
        }
    }

}
