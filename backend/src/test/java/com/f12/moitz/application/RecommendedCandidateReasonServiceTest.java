package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.port.LocationReasonGenerator;
import com.f12.moitz.application.port.dto.ReasonAndDescription;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendedCandidateReasonServiceTest {

    @Mock
    private LocationReasonGenerator locationReasonGenerator;

    @Test
    @DisplayName("추천 후보 이름과 태그로 이유를 생성한다")
    void generate() {
        final RecommendedCandidateReasonService service = new RecommendedCandidateReasonService(locationReasonGenerator);
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, List.of(CandidateSelectionTag.FAIRNESS),
                        samsung, List.of(CandidateSelectionTag.EFFICIENCY)
                )
        );
        final ReasonAndDescription seolleungReason = new ReasonAndDescription("#공평", "선릉역은 이동 시간이 고른 후보입니다.");
        final ReasonAndDescription samsungReason = new ReasonAndDescription("#평균최소", "삼성역은 평균 이동 시간이 짧은 후보입니다.");
        given(locationReasonGenerator.generateReasons(
                List.of("선릉역", "삼성역"),
                Map.of(
                        "선릉역", List.of(CandidateSelectionTag.FAIRNESS),
                        "삼성역", List.of(CandidateSelectionTag.EFFICIENCY)
                )
        )).willReturn(Map.of(
                "선릉역", seolleungReason,
                "삼성역", samsungReason
        ));

        final Map<Place, RecommendationReason> result = service.generate(recommendedCandidates);

        assertThat(result)
                .containsEntry(seolleung, new RecommendationReason("#공평", "선릉역은 이동 시간이 고른 후보입니다."))
                .containsEntry(samsung, new RecommendationReason("#평균최소", "삼성역은 평균 이동 시간이 짧은 후보입니다."));
        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<String>> namesCaptor = ArgumentCaptor.forClass(List.class);
        verify(locationReasonGenerator).generateReasons(namesCaptor.capture(), anyMap());
        assertThat(namesCaptor.getValue()).containsExactly("선릉역", "삼성역");
    }

    @Test
    @DisplayName("이유 생성 결과가 누락되면 제어된 예외를 반환한다")
    void generate_ThrowsExceptionWhenReasonIsMissing() {
        final RecommendedCandidateReasonService service = new RecommendedCandidateReasonService(locationReasonGenerator);
        final Place seolleung = place("선릉역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
        );
        given(locationReasonGenerator.generateReasons(
                List.of("선릉역"),
                Map.of("선릉역", List.of(CandidateSelectionTag.FAIRNESS))
        )).willReturn(Map.of());

        assertThatThrownBy(() -> service.generate(recommendedCandidates))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("placeName=선릉역");
    }

    @Test
    @DisplayName("이유 생성 결과가 null이면 제어된 예외를 반환한다")
    void generate_ThrowsExceptionWhenReasonsAreNull() {
        final RecommendedCandidateReasonService service = new RecommendedCandidateReasonService(locationReasonGenerator);
        final Place seolleung = place("선릉역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
        );
        given(locationReasonGenerator.generateReasons(
                List.of("선릉역"),
                Map.of("선릉역", List.of(CandidateSelectionTag.FAIRNESS))
        )).willReturn(null);

        assertThatThrownBy(() -> service.generate(recommendedCandidates))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("추천 이유 생성 결과가 null입니다.");
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
