package com.f12.moitz;

import com.f12.moitz.infrastructure.gemini.GoogleGeminiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoogleGeminiClientTest {

    @Autowired
    private GoogleGeminiClient geminiClient;

    @DisplayName("병렬 함수 호출 테스트")
    @Test
    void parallel() {
        //given
        String prompt = """
                Please rank the following subway stations based on the total number of PC bangs (internet cafes) and conversation-friendly cafes around them:
                - Samseong Station
                - Jamsil Station
                - Jamsil Saenae Station
                - Jamsil Naru Station
                
                Use the countPlacesByKeyword function to count how many PC bangs and cafes are located near each station. The radius is 500m.
                The station with more nearby places (combined count of PC bangs and cafes) should be ranked higher.
                Return the stations in ranked order with a brief explanation in Korean.
                """;

        // when & then
        geminiClient.generateWithParallelFunctionCalling(prompt);
    }

    @DisplayName("지역 추천 테스트")
    @Test
    void recommendLocations() {
        //given
        String prompt = """
                You're an AI assistant that recommends the most suitable meeting subway stations in the Seoul metropolitan area.
                
                You are given several starting subway stations. Do NOT evaluate these stations. Instead, use them to infer candidate destination stations where subway travel times from all starting points are **similar** (within a 15-minute difference between the longest and shortest travel time).
                
                The user may provide additional location preferences (e.g., “a place with quiet cafes”, “a place good for group study”, “somewhere with bars and restaurants”). These are called **user requirements**.
                
                Your task:
                
                1. Infer 10 candidate destination stations in the Seoul Metropolitan Subway system (excluding the given starting stations) where travel time from all starting stations is relatively similar.
                2. From the user requirements, extract **search keywords** that represent the type of places the user is looking for.
                   - Each keyword must be in **Korean** and consist of **only one word**.
                   - These keywords will be used with the `countPlacesByKeyword` function to evaluate nearby facilities.
                3. For each candidate station:
                   - Retrieve its coordinates.
                   - For each keyword, use the `countPlacesByKeyword` function to search within a 500m radius.
                   - Sum the total number of places matching all keywords to calculate a **requirement satisfaction score**.
                4. Rank the stations by their requirement satisfaction score.
                5. Return the **top %d** stations in ranked order.
                
                Each result must include:
                - Station name
                - Total facility count (based on keywords)
                - A brief explanation in **Korean** describing why it was selected, including both travel-time similarity and how well it meets the user’s stated requirements.
                
                Starting Stations (출발지): %s
                
                User Requirements (사용자 조건): %s
                """;

        String finalPrompt = String.format(prompt, 5, "잠실역, 여의도역, 혜화역", "곱창집이 많은 곳이면 좋겠어요.");

        //when & then
        geminiClient.generateWithParallelFunctionCalling(finalPrompt);
    }

    @DisplayName("장소 추천 테스트")
    @Test
    void recommendPlaces() {
        //given
        String prompt = """
                You're an AI assistant that recommends %d specific places within 500m of each given subway station.
           
            TASK OVERVIEW:
            You must complete this task in 2 phases:
            Phase 1: Data Collection (using function calls)
            Phase 2: Final Recommendation (generating JSON response)
            
            PHASE 1 - DATA COLLECTION:
            1. From the user requirements, extract search keywords that represent the types of places the user is looking for.
               - Each keyword must be in Korean and consist of only one word.
               - These keywords will be used with the getPlacesByKeyword function.
               - Among the search results, identify the top places based on their star ratings, as shown on the place URLs, and include their rankings using index values (e.g., 1 for the highest-rated place).
           
            2. For each station:
               - First, use getPointByPlaceName function to retrieve the station's coordinates.
               - Then, for each extracted keyword, use getPlacesByKeyword function to search for places within a 500m radius.
            
            PHASE 2 - FINAL RECOMMENDATION:
            After collecting all the data from function calls, you MUST generate the final recommendation.
            
            IMPORTANT: Even after receiving function call responses, you must continue to:
            1. Analyze all the collected place data
            2. For each station, select the top %d places based on:
   - Highest Star ratings 
   - Most Reviews 
                -Relevance to user requirements
            3. Assign index rankings (1 = best, 2 = second best, etc.)
            4. Generate the final JSON response
            
            CRITICAL OUTPUT FORMAT - READ CAREFULLY:
            Your FINAL response (after all function calls are complete) must be ONLY the JSON data with NO formatting whatsoever.
            
            Each recommendation must be returned strictly as raw JSON, with no surrounding text, explanation, or formatting
            
            
            Structure for your final response:
            {
                "responses": [
                    {
                        "stationName": "",
                        "places": [
                            {
                              "index": "",
                              "name": "",
                              "category": "",
                              "distance": "",
                              "url": ""
                            }
                        ]
                    }
                ]
            }
            
            Stations (지하철역): %s
            User Requirements (사용자 조건): %s
            
            EXECUTION INSTRUCTION:
            1. Start by calling the necessary functions to collect data
            2. After receiving all function responses, analyze the data and generate the final JSON recommendation
            3. Do NOT stop after function calls - you must provide the final recommendation JSON
            """;

        String finalPrompt = String.format(prompt, 3, 3, "잠실역, 여의도역, 혜화역", "곱창을 먹을 수 있는 곳이 많으면 좋겠어요.");

        //when & then
        geminiClient.generateWithParallelFunctionCalling(finalPrompt);
    }



    /*
    """
                You're an AI assistant that recommends %d specific places near each of the given subway stations.

                The user may provide additional place preferences (e.g., “a place with quiet cafes”, “a place good for group study”, “somewhere with bars and restaurants”). These are referred to as user requirements.

                Your task:
                1. From the user requirements, extract search keywords that represent the types of places the user is looking for.
                   - Each keyword must be in Korean and consist of only one word.
                   - These keywords will be used with the getPlacesByKeyword function to search for relevant places.
                2. For each station:
                   - Retrieve its coordinates.
                   - For each extracted keyword, use the getPlacesByKeyword function to search for places within a 500m radius.
                   - Among the search results, identify the top %d places based on their star ratings, as shown on the place URLs, and include their rankings using index values (e.g., 1 for the highest-rated place).
                   - Recommend these %d places per station.

                Each recommendation must be returned strictly as raw JSON, with no surrounding text, explanation, or formatting

                Expected JSON structure:
                {
                    "responses": [
                        {
                            "stationName": "",
                            "places": [
                                {
                                  "index": ""
                                  "name": "",
                                  "category": "",
                                  "distance": "",
                                  "url": ""
                                },
                              ...
                            ]
                        }
                        ...
                    ]
                }

                Stations (지하철역): %s
                User Requirements (사용자 조건): %s
                """
     */
}
