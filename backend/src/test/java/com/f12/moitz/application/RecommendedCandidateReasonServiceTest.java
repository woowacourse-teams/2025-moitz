package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.f12.moitz.application.recommendation.RecommendedCandidateReasonService;
import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import com.f12.moitz.domain.recommendation.RecommendedCandidates;
import com.f12.moitz.domain.recommendation.RecommendationReason;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedCandidateReasonServiceTest {

    @Test
    @DisplayName("추천 후보 이름과 태그로 이유를 생성한다")
    void generate() {
        final RecommendedCandidateReasonService service = new RecommendedCandidateReasonService();
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, List.of(CandidateSelectionTag.FAIRNESS),
                        samsung, List.of(CandidateSelectionTag.EFFICIENCY)
                )
        );

        final Map<Place, RecommendationReason> result = service.generate(recommendedCandidates);

        assertThat(result)
                .containsEntry(seolleung, new RecommendationReason(
                        "#공평",
                        "선릉역은 모든 참여자의 이동 시간이 최대한 비슷한 기준을 반영해 추천된 만남 장소입니다."
                ))
                .containsEntry(samsung, new RecommendationReason(
                        "#평균최소",
                        "삼성역은 전체 참여자의 평균 이동 시간이 짧은 기준을 반영해 추천된 만남 장소입니다."
                ));
    }

    @Test
    @DisplayName("추천 태그가 없으면 종합 추천 이유를 생성한다")
    void generate_UsesGeneralReasonWhenTagsAreMissing() {
        final RecommendedCandidateReasonService service = new RecommendedCandidateReasonService();
        final Place cityHall = place("시청역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(cityHall),
                Map.of()
        );

        final Map<Place, RecommendationReason> result = service.generate(recommendedCandidates);

        assertThat(result)
                .containsEntry(cityHall, new RecommendationReason(
                        "#종합추천",
                        "시청역은 이동 시간, 환승, 균형을 종합한 기준을 반영해 추천된 만남 장소입니다."
                ));
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
