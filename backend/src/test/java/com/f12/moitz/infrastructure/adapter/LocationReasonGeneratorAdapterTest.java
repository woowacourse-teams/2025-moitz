package com.f12.moitz.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.port.dto.ReasonAndDescription;
import com.f12.moitz.domain.CandidateSelectionTag;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationReasonGeneratorAdapterTest {

    private final LocationReasonGeneratorAdapter adapter = new LocationReasonGeneratorAdapter();

    @Test
    @DisplayName("추천 태그를 해시태그 설명과 문장 이유로 변환한다")
    void generateReasons_CreatesReasonAndDescriptionFromTags() {
        final Map<String, ReasonAndDescription> result = adapter.generateReasons(
                List.of("서울역"),
                Map.of("서울역", List.of(CandidateSelectionTag.TRANSFER, CandidateSelectionTag.EFFICIENCY))
        );

        assertThat(result.get("서울역"))
                .extracting(ReasonAndDescription::description, ReasonAndDescription::reason)
                .containsExactly(
                        "#최소환승 #최소평균",
                        "서울역은 환승 부담이 적은 기준, 전체 참여자의 평균 이동 시간이 짧은 기준을 반영해 추천된 만남 장소입니다."
                );
    }

    @Test
    @DisplayName("추천 태그가 없으면 종합 추천 이유를 생성한다")
    void generateReasons_UsesGeneralReasonWhenTagsAreMissing() {
        final Map<String, ReasonAndDescription> result = adapter.generateReasons(
                List.of("시청역"),
                Map.of()
        );

        assertThat(result.get("시청역"))
                .extracting(ReasonAndDescription::description, ReasonAndDescription::reason)
                .containsExactly(
                        "#종합추천",
                        "시청역은 이동 시간, 환승, 균형을 종합한 기준을 반영해 추천된 만남 장소입니다."
                );
    }

    @Test
    @DisplayName("종합 추천 태그는 다른 추천 태그가 없는 경우에만 이유에 포함한다")
    void generateReasons_RemovesGeneralReasonWhenOtherTagsExist() {
        final Map<String, ReasonAndDescription> result = adapter.generateReasons(
                List.of("서울역"),
                Map.of("서울역", List.of(CandidateSelectionTag.TRANSFER, CandidateSelectionTag.GENERAL))
        );

        assertThat(result.get("서울역"))
                .extracting(ReasonAndDescription::description, ReasonAndDescription::reason)
                .containsExactly(
                        "#최소환승",
                        "서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다."
                );
    }
}
