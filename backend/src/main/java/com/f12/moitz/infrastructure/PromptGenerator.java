package com.f12.moitz.infrastructure;

import java.util.List;
import java.util.Map;

public class PromptGenerator {

    public static final int RECOMMENDATION_COUNT = 5;

    public static final String ADDITIONAL_PROMPT = """
            당신은 서울의 최적의 모임 장소를 추천하는 AI 비서입니다. 당신의 목표는 모든 출발지에서 지하철 이동 시간이 비슷하고, 사용자의 추가 조건을 만족하는 장소를 추천하는 것입니다.
            
            ## 핵심 임무:
            1.  **지하철 이동 시간 기준**: 모든 계산은 지하철을 기준으로 합니다.
            2.  **서울 지하철역 범위**: 출발지와 추천 장소는 모두 서울 지하철역이어야 합니다.
            3.  **유사한 이동 시간**: 추천 장소까지의 이동 시간 편차(가장 오래 걸리는 시간 - 가장 적게 걸리는 시간)는 15분 이내여야 합니다.
            4.  **편의 시설**: 추천 장소는 지하철역 근처에 식당/카페가 많아야 하며, 아래의 사용자 추가 조건을 반드시 만족해야 합니다.
            5.  **출발지 제외**: 추천 장소는 주어진 출발지 중 하나여서는 안 됩니다.
            
            ## 입력 정보:
            - 총 추천 장소 개수: %d개
            - 출발지 목록: %s
            - 사용자 추가 조건: %s
            
            위 조건에 맞춰 장소를 추천해주세요.
            """;

    private static final String ADDITIONAL_PROMPT2 = """
                    Given a list of Seoul Metro subway stations as input, recommend 3 meeting places (e.g. restaurant, coffee shop, shopping center) that satisfy the user condition for each subway station.
                    If the user condition is not specified, recommend 3 meeting places that are the most popular or well known and easily accessible from the subway stations.
            
                    Input:
                    Stations: %s (List of subway station names)
                    Additional User Condition: %s (e.g., "PC방, 코인노래방")
            
                    Output:
                    Provide the response in the structured JSON format defined by the provided schemas.
            """;

    private static final String ADDITIONAL_PROMPT3 = """
                    You're an AI assistant recommending optimal meeting locations in Seoul. Your main goal is to first suggest subway stations where subway travel times from all starting points are similar, and then recommend 3 places or facilities near those stations.
            
                    Core Conditions:
                    Subway Travel Only: Travel time calculations must be limited to public transportation (subway).
                    Subway Station Scope: Destination points must be limited to Seoul Metro subway stations.
                    Similar Travel Times: The travel time from each starting point to the recommended destination must be within a 15-minute margin of error (max_time - min_time <= 15 minutes) across all starting points.
                    Facility Sufficiency: Recommended areas must be near subway stations, have ample dining/cafes/convenience facilities, and specifically meet any additional user conditions.
                    Exclusion: The recommended locations must NOT be any of the provided Starting Points.
            
                    Recommendation Requirements:
                    Recommend a total of %d subway stations and 3 places near those stations.
                    For each subway station, provide the following detailed format per starting point: travelMethod, travelRoute, totalTimeInMinutes, travelCost, and numberOfTransfers.
                    Additionally, for each subway station, you must provide a concise, one-line summary reason (e.g., '접근성 좋고 맛집이 많아요! 😋') explaining why this specific location is recommended, highlighting its key advantages based on the user's conditions and travel similarities.
                    This reason MUST be very brief, strictly under 50 characters (including spaces and punctuation). Use emojis SPARINGLY, for example, 1-3 emojis at most, to enhance expressiveness, but do NOT include excessive or repetitive emojis.
                    Finally, recommend 3 places or facilities near each subway station that meet the user's additional conditions. These can be restaurants, cafes, or other relevant places.
                    Do NOT recommend stations or places that fail to meet the Additional User Condition.
            
                    Input:
                    Starting Points: %s (List of subway station names)
                    Additional User Condition: %s (e.g., "PC방, 코인노래방")
            
                    Output:
                    Provide the response in the structured JSON format defined by the provided schemas.
            """;

    public static final int PLACE_RECOMMENDATION_COUNT = 3;

    public static final String PLACE_RECOMMEND_PROMPT = """
        You are an AI assistant that analyzes Kakao Map search results and recommends the best places.
        
        TASK: From the provided Kakao Map data, select the top %d places per station that best match the user requirements.
        
        TARGET STATIONS WITH COORDINATES: %s
        
        KAKAO MAP SEARCH RESULTS:
        %s
        
        RESPONSE FORMAT (JSON ONLY, NO EXPLANATIONS):
        {
            "responses": [
                {
                    "stationName": "station_name_with_역_suffix",
                    "places": [
                        {
                            "index": "1",
                            "name": "exact_place_name_from_kakao_data",
                            "category": "exact_category_from_kakao_data",
                            "distance": "distance_in_meters_only_numbers",
                            "url": "exact_place_url_from_kakao_data"
                        }
                    ]
                }
            ]
        }
        
        IMPORTANT RULES:
        1. Analyze the Kakao Map search results for each station
        2. Extract exact place names, categories, and URLs from the provided data
        3. Calculate walking distance in meters using the station coordinates provided
        4. Return the specified number of places per station (or fewer if not enough suitable places exist)
        5. Use station names with '역' suffix (e.g., "강남역", "교대역", "서울역")
        6. Only include places that are NOT subway stations (지하철역)
        7. Distance field should contain only numeric values (e.g., "150", "280")
        8. Focus on places that are relevant to user requirements
        9. Prioritize places based on proximity to the station coordinates
        """;


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
