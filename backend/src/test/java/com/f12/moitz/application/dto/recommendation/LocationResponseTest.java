package com.f12.moitz.application.dto.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
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

        assertThat(json.get("tag").asText()).isEqualTo(CandidateSelectionTag.TRANSFER.name());
        assertThat(json.has("tags")).isFalse();
        assertThat(json.get("tagInfo").asText()).isEqualTo(CandidateSelectionTag.TRANSFER.getDescription());
        assertThat(json.get("locationInfo").asText()).isEqualTo(response.reason());
    }

    @Test
    void serialize_WithTagInfoAndLocationInfo_ListInput() throws Exception {
        final LocationResponse response = new LocationResponse(
                1L,
                1,
                37.0,
                127.0,
                "서울역",
                20,
                false,
                List.of("EFFICIENCY"),
                "#최소평균",
                "서울역은 전체 참여자의 평균 이동 시간이 짧은 기준을 반영해 추천된 만남 장소입니다.",
                Map.of(),
                List.of()
        );

        final JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.get("tag").asText()).isEqualTo(CandidateSelectionTag.EFFICIENCY.name());
        assertThat(json.has("tags")).isFalse();
        assertThat(json.get("tagInfo").asText()).isEqualTo(CandidateSelectionTag.EFFICIENCY.getDescription());
        assertThat(json.get("locationInfo").asText()).isEqualTo(response.reason());
    }

    @Test
    void create_ThrowsBadRequestExceptionWhenTagsHaveMultipleValues() {
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
                .isInstanceOfSatisfying(BadRequestException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG)
                );
    }

    @Test
    void create_ThrowsBadRequestExceptionWhenTagsAreEmpty() {
        assertThatThrownBy(() -> new LocationResponse(
                1L,
                1,
                37.0,
                127.0,
                "서울역",
                20,
                false,
                List.of(),
                "#최소환승",
                "서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.",
                Map.of(),
                List.of()
        ))
                .isInstanceOfSatisfying(BadRequestException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG)
                );
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

    @Test
    void serialize_WithTagInfoAndLocationInfo_InvalidTagThrows() {
        assertThatThrownBy(() -> new LocationResponse(
                1L,
                1,
                37.0,
                127.0,
                "서울역",
                20,
                false,
                List.of("INVALID_TAG"),
                "#알수없음",
                "서울역 추천 이유입니다.",
                Map.of(),
                List.of()
        ))
                .isInstanceOfSatisfying(BadRequestException.class, exception -> {
                    assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INPUT_INVALID_RECOMMENDATION_TAG);
                    assertThat(exception).hasMessageContaining("INVALID_TAG");
                    assertThat(exception).hasCauseInstanceOf(IllegalArgumentException.class);
                });
    }

}
