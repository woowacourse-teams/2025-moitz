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


    @DisplayName("병렬 함수 호출 테스트")
    @Test
    void parallel() {
        //given
        String prompt = "Find all places for climbing within a 500m radius of Gangnam Station. Use multiple function calls to generate a response if necessary.";

        //when
        geminiClient.generateWithFunctionCalling(prompt);

        //then

    }
}
