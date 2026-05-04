package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.CandidateSelectionTag;
import com.f12.moitz.domain.Place;
import com.f12.moitz.domain.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedCandidatesTest {

    @Test
    @DisplayName("추천 후보 장소 기준으로 태그를 정규화한다")
    void constructor_NormalizesTagsByRecommendedCandidatePlaces() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final Place gangnam = place("강남역");

        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, List.of(CandidateSelectionTag.FAIRNESS, CandidateSelectionTag.GENERAL),
                        gangnam, List.of(CandidateSelectionTag.TRANSFER)
                )
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedCandidates.getPlaces())
                    .containsExactly(seolleung, samsung);
            softAssertions.assertThat(recommendedCandidates.getTags(seolleung))
                    .containsExactly(CandidateSelectionTag.FAIRNESS);
            softAssertions.assertThat(recommendedCandidates.getTags(samsung))
                    .containsExactly(CandidateSelectionTag.GENERAL);
            softAssertions.assertThat(recommendedCandidates.size()).isEqualTo(2);
            softAssertions.assertThat(recommendedCandidates.isEmpty()).isFalse();
        });
    }

    @Test
    @DisplayName("추천 후보 태그가 비어있거나 null만 있으면 종합 추천 태그로 보정한다")
    void constructor_UsesGeneralWhenTagsAreEmptyOrOnlyNull() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");

        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, List.of(),
                        samsung, Arrays.asList(null, null)
                )
        );

        assertThat(recommendedCandidates.getTags(seolleung))
                .containsExactly(CandidateSelectionTag.GENERAL);
        assertThat(recommendedCandidates.getTags(samsung))
                .containsExactly(CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("추천 후보 이름 목록과 이름별 태그를 조회한다")
    void getPlaceNamesAndTagsByPlaceName() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, List.of(CandidateSelectionTag.FAIRNESS),
                        samsung, List.of(CandidateSelectionTag.EFFICIENCY, CandidateSelectionTag.GENERAL)
                )
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedCandidates.getPlaceNames())
                    .containsExactly("선릉역", "삼성역");
            softAssertions.assertThat(recommendedCandidates.getTagsByPlaceName())
                    .containsEntry("선릉역", List.of(CandidateSelectionTag.FAIRNESS))
                    .containsEntry("삼성역", List.of(CandidateSelectionTag.EFFICIENCY))
                    .hasSize(2);
        });
    }

    @Test
    @DisplayName("추천 후보 이름별 태그는 외부에서 변경할 수 없다")
    void getTagsByPlaceName_ReturnsUnmodifiableMap() {
        final Place seolleung = place("선릉역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
        );

        assertThatThrownBy(() -> recommendedCandidates.getTagsByPlaceName().put(
                "삼성역",
                List.of(CandidateSelectionTag.EFFICIENCY)
        ))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("추천 후보 목록과 태그 맵은 null일 수 없다")
    void constructor_ThrowsExceptionWhenArgumentsAreInvalid() {
        final Place seolleung = place("선릉역");

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidates(null, Map.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 목록은 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidates(Arrays.asList(seolleung, null), Map.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 목록에 null이 포함될 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> new RecommendedCandidates(List.of(seolleung), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 태그는 null일 수 없습니다.");
        });
    }

    @Test
    @DisplayName("추천 후보 목록은 외부에서 변경할 수 없다")
    void getPlaces_ReturnsUnmodifiableList() {
        final Place seolleung = place("선릉역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
        );

        assertThatThrownBy(() -> recommendedCandidates.getPlaces().add(place("삼성역")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("추천 후보가 아닌 장소의 태그는 조회할 수 없다")
    void getTags_ThrowsExceptionWhenPlaceIsNotRecommendedCandidate() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, List.of(CandidateSelectionTag.FAIRNESS))
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> recommendedCandidates.getTags(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 장소는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> recommendedCandidates.getTags(samsung))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 태그가 누락되었습니다. 추천 지역: 삼성역");
        });
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
