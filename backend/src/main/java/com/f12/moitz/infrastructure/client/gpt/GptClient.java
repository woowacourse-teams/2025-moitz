package com.f12.moitz.infrastructure.client.gpt;

import com.f12.moitz.infrastructure.client.gemini.dto.RecommendedLocationResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GptClient {
    public RecommendedLocationResponse generateResponse(final List<String> startPlaceNames, final String condition) {
        return null;
    }
}
