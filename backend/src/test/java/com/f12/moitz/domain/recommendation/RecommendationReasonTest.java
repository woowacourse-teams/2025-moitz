package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationReasonTest {

    @Test
    @DisplayName("추천 태그를 해시태그 설명과 문장 이유로 변환한다")
    void fromSelectionTags_CreatesReasonFromTags() {
        final RecommendationReason reason = RecommendationReason.fromSelectionTags(
                "서울역",
                List.of(CandidateSelectionTag.TRANSFER, CandidateSelectionTag.EFFICIENCY)
        );

        assertThat(reason.description()).isEqualTo("#최소환승 #평균최소");
        assertThat(reason.reason())
                .isEqualTo("서울역은 환승 부담이 적은 기준, 전체 참여자의 평균 이동 시간이 짧은 기준을 반영해 추천된 만남 장소입니다.");
    }

    @Test
    @DisplayName("추천 태그가 없으면 종합 추천 이유를 생성한다")
    void fromSelectionTags_UsesGeneralReasonWhenTagsAreMissing() {
        final RecommendationReason reason = RecommendationReason.fromSelectionTags("시청역", List.of());

        assertThat(reason.description()).isEqualTo("#종합추천");
        assertThat(reason.reason())
                .isEqualTo("시청역은 이동 시간, 환승, 균형을 종합한 기준을 반영해 추천된 만남 장소입니다.");
    }

    @Test
    @DisplayName("종합 추천 태그는 다른 추천 태그가 없는 경우에만 이유에 포함한다")
    void fromSelectionTags_RemovesGeneralReasonWhenOtherTagsExist() {
        final RecommendationReason reason = RecommendationReason.fromSelectionTags(
                "서울역",
                List.of(CandidateSelectionTag.TRANSFER, CandidateSelectionTag.GENERAL)
        );

        assertThat(reason.description()).isEqualTo("#최소환승");
        assertThat(reason.reason())
                .isEqualTo("서울역은 환승 부담이 적은 기준을 반영해 추천된 만남 장소입니다.");
    }

    @Test
    @DisplayName("추천 장소 이름이 비어있으면 추천 이유를 생성할 수 없다")
    void fromSelectionTags_ThrowsExceptionWhenPlaceNameIsBlank() {
        assertThatThrownBy(() -> RecommendationReason.fromSelectionTags(" ", List.of(CandidateSelectionTag.FAIRNESS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추천 장소 이름은 비어 있을 수 없습니다.");
    }

}
