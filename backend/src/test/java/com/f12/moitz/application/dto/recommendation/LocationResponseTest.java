package com.f12.moitz.application.dto.recommendation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LocationResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serialize_WithTagInfoAndLocationInfo() throws Exception {
        final LocationResponse response = new LocationResponse(
                1L,
                1,
                37.0,
                127.0,
                "서울역",
                20,
                false,
                "TRANSFER",
                "#최소환승",
                "서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.",
                Map.of(),
                List.of()
        );

        final JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.get("tag_info").asText()).isEqualTo("환승 부담이 적은 기준");
        assertThat(json.get("location_info").asText()).isEqualTo(response.reason());
        assertThat(json.has("tagInfo")).isFalse();
        assertThat(json.has("locationInfo")).isFalse();
    }

}
