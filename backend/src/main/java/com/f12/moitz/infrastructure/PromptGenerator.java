package com.f12.moitz.infrastructure;

import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;

import java.util.List;
import java.util.Map;

public class PromptGenerator {

    public static final int RECOMMENDATION_COUNT = 5;

    public static final String ADDITIONAL_PROMPT = """
            당신은 서울의 최적의 모임 장소를 추천하는 AI 비서입니다.
            당신의 목표는 모든 출발지에서 지하철 이동 시간이 비슷하고, 사용자의 추가 조건을 만족하는 장소를 추천하는 것입니다.
            단, 추천 장소는 주어진 출발지 중 하나여서는 안 됩니다.
            
            ## 입력 정보:
            - 총 추천 장소 개수: %d개
            - 출발지 목록: %s
            - 사용자 추가 조건: %s
            
            위 조건에 맞춰 장소를 추천해주세요.
            """;

    public static final String PLACE_FILTER_PROMPT = """
            You are an AI assistant that analyzes Kakao Map search results and recommends the best places.
            
            TASK: From the provided Kakao Map data for %s, select the top %d places that best match the user requirements.
            
            STATION: %s
            STATION COORDINATES: (%.6f, %.6f)
            USER REQUIREMENTS: %s
            
            KAKAO MAP SEARCH RESULTS:
            %s
            
            RESPONSE FORMAT (JSON ONLY, NO EXPLANATIONS):
            {
                "places": [
                    {
                        "index": 1,
                        "name": "exact_place_name_from_kakao_data",
                        "category": "exact_category_from_kakao_data",
                        "distance": distance,
                        "url": "exact_place_url_from_kakao_data"
                    }
                ]
            }
            
            IMPORTANT RULES:
            1. Analyze the Kakao Map search results for the station
            2. Extract exact place names, categories, and URLs from the provided data
            4. Return exactly %d places (or fewer if not enough suitable places exist)
            6. distance field should contain only numeric values (e.g., 2, 5, 8)
            7. Focus on places that match the user requirements
            8. Prioritize places based on proximity to the station coordinates and relevance
            9. Use the exact place information from the search results provided above
            """;

    public static String FORMAT_SINGLE_PLACE_TO_PROMPT(Place place, List<KakaoApiResponse> kakaoResponses) {
        StringBuilder sb = new StringBuilder();
        sb.append("KAKAO MAP SEARCH RESULTS:\n");
        sb.append("========================\n\n");

        String stationName = place.getName();
        sb.append(String.format("=== %s ===\n", stationName));
        sb.append(String.format("Station Coordinates: (%.6f, %.6f)\n",
                place.getPoint().getX(), place.getPoint().getY()));
        sb.append("Search Results:\n");

        for (int i = 0; i < kakaoResponses.size(); i++) {
            KakaoApiResponse response = kakaoResponses.get(i);
            sb.append(String.format("Response %d: %s\n", i + 1, response.toString()));
        }

        sb.append("\n========================\n");
        sb.append("RECOMMENDATION CRITERIA:\n");
        sb.append(
                "Please analyze the above Kakao Map data and recommend the BEST places based on the following priority:\n\n");
        sb.append("RESPONSE FORMAT:\n");
        sb.append(
                "- IMPORTANT: Use the exact station name shown above (with '역' suffix if applicable) in your response.\n");
        sb.append("- For each recommendation, include: exact place name, category, distance, and URL from the data.\n");
        sb.append("- Rank recommendations by their proximity to the station (closest first).\n");
        sb.append("- The recommendation should be based **only** on the data provided above.\n\n");

        return sb.toString();
    }

    public static final int PLACE_RECOMMENDATION_COUNT = 3;

    public static Map<String, Object> getSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "recommendations", Map.of(
                                "type", "array",
                                "description",
                                "추천된 장소들의 이름 리스트. 총 N개의 지하철역 이름(문자열)을 포함합니다.",
                                "items", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "locationName", Map.of("type", "string", "description", "추천 장소의 이름"),
                                                "reason", Map.of(
                                                        "type", "string",
                                                        "description",
                                                        "해당 장소를 추천하는 간결한 한 줄 요약 이유 20자 이내, 어울리는 이모지 1개와 함께 (예: '접근성 좋고 맛집이 많아요! 😋') 만약 추천 이유에 사용자 조건이 포함되어 있다면, 이유에 명시할 것",
                                                        "maxLength", 20
                                                ),
                                                "description", Map.of(
                                                        "type", "string",
                                                        "description", "추천 장소에 대한 간단한 설명 100자 이내 (예: '강남역은 하루 유동 인구가 많은 번화가로, 다양한 연령층이 많이 이용하며, 주변에는 대형 빌딩, 쇼핑몰, 학원, 음식점 등이 밀집되어 있습니다. ')",
                                                        "maxLength", 100
                                                )
                                        ),
                                        "required", List.of("locationName", "reason")
                                ),
                                "minItems", 3,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations")
        );
    }

}
