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

    @DisplayName("제미나이 함수 호출 테스트")
    @Test
    void functionCalling() {
        //given
        String prompt = "Find all places for climbing within a 500m radius of Gangnam Station, located at longitude 127.028307900881 and latitude 37.4981646510326.";

        //when & then
        geminiClient.generateWithFunctionCalling(prompt);
    }

    @DisplayName("제미나이 함수 연속 호출 테스트")
    @Test
    void multipleFunctionCalling() {
        //given
        String prompt = "Find all places for climbing within a 500m radius of Gangnam Station.";

        //when & then
        geminiClient.generateWithFunctionCalling(prompt);
    }

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
                Return the stations in ranked order with a brief explanation.
                """;

        //when & then
        geminiClient.generateWithParallelFunctionCalling(prompt);
    }
}
