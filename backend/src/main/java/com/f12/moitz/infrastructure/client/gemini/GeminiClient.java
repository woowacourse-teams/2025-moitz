package com.f12.moitz.infrastructure.client.gemini;

import com.f12.moitz.application.dto.RecommendedLocationsResponse;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.util.List;

public interface GeminiClient {

    GenerateContentResponse generate(List<Content> contents, GenerateContentConfig config);

    RecommendedLocationsResponse generateResponse(
            List<String> startingPlaces,
            List<String> candidatePlaces,
            List<String> requirements
    );
}
