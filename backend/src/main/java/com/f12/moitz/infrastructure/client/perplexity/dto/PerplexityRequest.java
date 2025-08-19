package com.f12.moitz.infrastructure.client.perplexity.dto;

import java.util.List;
import java.util.Map;

public record PerplexityRequest(
        String model,
        List<Message> messages,
        Map<String, Object> response_format
) {

    public record Message(
            String role,
            String content
    ) {

    }

}
