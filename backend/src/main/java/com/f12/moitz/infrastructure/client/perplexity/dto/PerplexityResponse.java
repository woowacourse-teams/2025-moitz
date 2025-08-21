package com.f12.moitz.infrastructure.client.perplexity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PerplexityResponse(
        String id,
        String model,
        long created,
        Usage usage,
        @JsonProperty("search_results") List<SearchResult> searchResults,
        String object,
        List<Choice> choices
) {

    public record Usage(
            @JsonProperty("prompt_tokens") int promptTokens,
            @JsonProperty("completion_tokens") int completionTokens,
            @JsonProperty("total_tokens") int totalTokens,
            @JsonProperty("search_context_size") String searchContextSize
    ) {

    }

    public record SearchResult(
            String title,
            String url,
            String date,
            @JsonProperty("last_updated") String lastUpdated
    ) {

    }

    public record Choice(
            int index,
            @JsonProperty("finish_reason") String finishReason,
            Message message,
            Delta delta
    ) {

    }

    public record Message(
            String role,
            String content
    ) {

    }

    public record Delta(
            String role,
            String content
    ) {

    }

}
