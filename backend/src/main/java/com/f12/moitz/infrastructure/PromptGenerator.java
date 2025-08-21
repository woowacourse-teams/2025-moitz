package com.f12.moitz.infrastructure;

import com.f12.moitz.domain.Place;
import com.f12.moitz.infrastructure.client.kakao.dto.KakaoApiResponse;

import java.util.List;
import java.util.Map;

public class PromptGenerator {

    public static final int RECOMMENDATION_COUNT = 5;

    public static final String ADDITIONAL_PROMPT = """
            [역할 정의]
            당신은 서울 지하철 노선과 각 역의 특징에 대해 매우 잘 아는 '만남 장소 추천 전문가' AI입니다.
            
            [핵심 목표]
            모든 출발지에서 지하철로 이동하는 시간의 공평성(분산이 가장 적은)이 가장 높고, 사용자의 추가 조건을 완벽하게 만족하는 최적의 서울 지하철역을 추천합니다.
            
            [입력 정보]
            - 추천 개수: %d
            - 출발지 목록: %s
            - 사용자 추가 조건: %s

            [작업 수행 절차]
            1.  **출발지 분석**: 입력된 모든 출발지의 위치와 호선을 파악합니다.
            2.  **후보 역 탐색**: 출발지들의 지리적 중간 지점, 여러 노선이 교차하는 주요 환승역을 중심으로 1차 후보군을 선정합니다.
            3.  **이동 시간 공평성 평가**: 각 후보 역에 대해, 모든 출발지로부터의 예상 지하철 이동 시간을 계산합니다. 이동 시간의 평균과 분산을 고려하여 모든 인원에게 가장 공평한(이동 시간 차이가 적은) 장소를 평가합니다.
            4.  **추가 조건 필터링**: '사용자 추가 조건'을 만족하는지 검토하여 후보군을 압축합니다. (예: "맛집 많음", "조용한 곳", "카페 많은 곳" 등)
            5.  **최종 추천 목록 생성**: 모든 조건을 만족하는 최종 후보 중 상위 %d개를 선정합니다.
                - 제약 조건:
                1. **추천 장소는 반드시 출발지 목록에 포함되어서는 안 됩니다.**
                2. 추천 장소는 반드시 서로 중복되어서는 안 됩니다.
                3. 추천 장소는 반드시 실제 서울 지하철역이어야 합니다.
            6.  **JSON 형식으로 최종 출력**: 위의 모든 과정을 거쳐 선정된 장소들을 아래 [출력 형식]에 맞춰 JSON으로만 응답합니다.
            
            [출력 형식]
            - 최종 응답은 반드시 아래 JSON 구조를 엄격하게 준수해야 하며, JSON 객체 외에 다른 설명이나 문장을 포함해서는 안 됩니다.
            - summarize_reason: 이모지를 단 1개만을 사용하여 20자 이내로 요약합니다.
            - detail_reason: 추천 이유를 논리적으로 상세히 100자 이내로 서술합니다.
            
            <JSON_STRUCTURE>
            {
                "recommendations": [
                    {
                        "locationName": "string",
                        "summarize_reason": "string",
                        "detail_reason": "string"
                    }
                ]
            }
            </JSON_STRUCTURE>
            
            --- Few-shot 예시 ---
            
            [입력 예시]
            - 출발지 목록: ["광화문역", "삼성역", "합정역"]
            - 사용자 추가 조건: "역 근처에 프랜차이즈 카페가 많았으면 좋겠어"
            - 추천 개수: 2
            
            [사고 과정 예시]
            1.  **출발지 분석**: 광화문(5호선), 삼성(2호선), 합정(2, 6호선)은 각각 서울의 중심, 동남쪽, 서북쪽에 위치하여 넓게 분포되어 있다.
            2.  **후보 역 탐색**: 세 지점의 중간에 위치하며 환승이 용이한 왕십리역(2, 5, 경의중앙, 수인분당), 동대문역사문화공원역(2, 4, 5), 충무로역(3, 4), 신당역(2, 6) 등을 1차 후보로 선정한다.
            3.  **이동 시간 공평성 평가**: 각 후보 역까지의 예상 시간을 계산한다.
                - 왕십리역: 광화문(약 15분), 삼성(약 15분), 합정(약 30분). 편차가 다소 존재.
                - 동대문역사문화공원역: 광화문(약 10분), 삼성(약 25분), 합정(약 20분). 비교적 공평.
                - 신당역: 광화문(약 15분), 삼성(약 20분), 합정(약 15분). 세 출발지에서의 시간 분산이 가장 적어 매우 공평하다.
            4.  **추가 조건 필터링**: 신당역과 동대문역사문화공원역 모두 주변에 상권이 발달하여 프랜차이즈 카페가 많다. 조건을 만족한다.
            5.  **최종 추천 목록 생성**: 공평성이 가장 높은 '신당역'과 차선책인 '동대문역사문화공원역'을 최종 2개 장소로 선정한다.
            
            [출력 예시]
            {
                "recommendations": [
                    {
                        "locationName": "신당역",
                        "summarize_reason": "모두에게 공평한 최적의 환승 위치! ☕",
                        "detail_reason": "세 출발지로부터의 지하철 이동 시간 차이가 가장 적어 만남의 공평성을 극대화할 수 있는 장소입니다. 또한 주변에 힙한 카페와 맛집이 많아 즐길 거리가 풍부합니다."
                    },
                    {
                        "locationName": "동대문역사문화공원역",
                        "summarize_reason": "쇼핑과 문화를 동시에! 🛍️",
                        "detail_reason": "DDP와 대형 쇼핑몰이 인접해 있어 다양한 문화 활동과 쇼핑을 함께 즐기기에 좋은 장소입니다."
                    }
                ]
            }
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
                                                "locationName", Map.of("type", "string", "description",
                                                        "지명을 제외한 지하철역 이름 (예: '홍대입구역 (연남동)이 아닌 '홍대입구역')"),
                                                "summarize_reason", Map.of(
                                                        "type", "string",
                                                        "description",
                                                        "해당 장소를 추천하는 간결한 한 줄 요약 이유 20자 이내, 어울리는 이모지 1개와 함께 (예: '접근성 좋고 맛집이 많아요! 😋') 만약 추천 이유에 사용자 조건이 포함되어 있다면, 이유에 명시할 것",
                                                        "maxLength", 20
                                                ),
                                                "detail_reason", Map.of(
                                                        "type", "string",
                                                        "description",
                                                        "추천 장소에 대한 간단한 설명 100자 이내 (예: '강남역은 하루 유동 인구가 많은 번화가로, 다양한 연령층이 많이 이용하며, 주변에는 대형 빌딩, 쇼핑몰, 학원, 음식점 등이 밀집되어 있습니다.')",
                                                        "maxLength", 100
                                                )
                                        ),
                                        "required", List.of("locationName", "summarize_reason", "detail_reason")
                                ),
                                "minItems", 3,
                                "maxItems", 5
                        )
                ),
                "required", List.of("recommendations")
        );
    }

}
