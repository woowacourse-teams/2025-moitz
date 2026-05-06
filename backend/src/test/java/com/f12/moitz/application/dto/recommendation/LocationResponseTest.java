package com.f12.moitz.application.dto.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
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

    @Test
    void create_ThrowsExceptionWhenTagsHaveMultipleValues() {
        assertThatThrownBy(() -> new LocationResponse(
                1L,
                1,
                37.0,
                127.0,
                "서울역",
                20,
                false,
                List.of("TRANSFER", "EFFICIENCY"),
                "#최소환승",
                "서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.",
                Map.of(),
                List.of()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 태그는 하나만 가질 수 있습니다.");
    }

    @Test
    void create_ThrowsBadRequestExceptionWhenTagIsUnknown() {
        assertThatThrownBy(() -> new LocationResponse(
                1L,
                1,
                37.0,
                127.0,
                "서울역",
                20,
                false,
                "UNKNOWN",
                "#알수없음",
                "서울역 추천 이유입니다.",
                Map.of(),
                List.of()
        ))
                .isInstanceOfSatisfying(BadRequestException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG);
                    assertThat(exception).hasMessageContaining("UNKNOWN");
                    assertThat(exception).hasCauseInstanceOf(IllegalArgumentException.class);
                });
    }

}
