package com.f12.moitz.domain.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.f12.moitz.domain.recommendation.candidate.CandidateSelectionTag;
import com.f12.moitz.domain.place.Place;
import com.f12.moitz.domain.place.Point;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendedCandidatesTest {

    @Test
    @DisplayName("추천 후보 장소 기준으로 단일 태그를 정규화한다")
    void constructor_NormalizesTagByRecommendedCandidatePlaces() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final Place gangnam = place("강남역");

        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, CandidateSelectionTag.FAIRNESS,
                        gangnam, CandidateSelectionTag.TRANSFER
                )
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedCandidates.getPlaces())
                    .containsExactly(seolleung, samsung);
            softAssertions.assertThat(recommendedCandidates.getTag(seolleung))
                    .isEqualTo(CandidateSelectionTag.FAIRNESS);
            softAssertions.assertThat(recommendedCandidates.getTag(samsung))
                    .isEqualTo(CandidateSelectionTag.GENERAL);
            softAssertions.assertThat(recommendedCandidates.size()).isEqualTo(2);
            softAssertions.assertThat(recommendedCandidates.isEmpty()).isFalse();
        });
    }

    @Test
    @DisplayName("추천 후보 태그가 없거나 null이면 적당한 추천 태그로 보정한다")
    void constructor_UsesGeneralWhenTagsAreMissingOrNull() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final Map<Place, CandidateSelectionTag> tagsByPlace = new LinkedHashMap<>();
        tagsByPlace.put(samsung, null);

        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                tagsByPlace
        );

        assertThat(recommendedCandidates.getTag(seolleung))
                .isEqualTo(CandidateSelectionTag.GENERAL);
        assertThat(recommendedCandidates.getTag(samsung))
                .isEqualTo(CandidateSelectionTag.GENERAL);
    }

    @Test
    @DisplayName("추천 후보 이름 목록과 이름별 단일 태그를 조회한다")
    void getPlaceNamesAndTagsByPlaceName() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, CandidateSelectionTag.FAIRNESS,
                        samsung, CandidateSelectionTag.EFFICIENCY
                )
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(recommendedCandidates.getPlaceNames())
                    .containsExactly("선릉역", "삼성역");
            softAssertions.assertThat(recommendedCandidates.getTagByPlaceName())
                    .containsEntry("선릉역", CandidateSelectionTag.FAIRNESS)
                    .containsEntry("삼성역", CandidateSelectionTag.EFFICIENCY)
                    .hasSize(2);
        });
    }

    @Test
    @DisplayName("추천 후보 이름별 태그는 외부에서 변경할 수 없다")
    void getTagByPlaceName_ReturnsUnmodifiableMap() {
        final Place seolleung = place("선릉역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, CandidateSelectionTag.FAIRNESS)
        );

        assertThatThrownBy(() -> recommendedCandidates.getTagByPlaceName().put(
                "삼성역",
                CandidateSelectionTag.EFFICIENCY
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
                Map.of(seolleung, CandidateSelectionTag.FAIRNESS)
        );

        assertThatThrownBy(() -> recommendedCandidates.getPlaces().add(place("삼성역")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("추천 후보 이름과 태그로 추천 이유를 생성한다")
    void createReasons() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung, samsung),
                Map.of(
                        seolleung, CandidateSelectionTag.FAIRNESS,
                        samsung, CandidateSelectionTag.EFFICIENCY
                )
        );

        final Map<Place, RecommendationReason> result = recommendedCandidates.createReasons();

        assertThat(result)
                .containsEntry(seolleung, new RecommendationReason(
                        "#가장공평",
                        "선릉역은 모든 참여자의 이동 시간이 가장 공평한 기준을 반영해 추천된 만남 장소입니다."
                ))
                .containsEntry(samsung, new RecommendationReason(
                        "#최소평균",
                        "삼성역은 전체 참여자의 평균 이동 시간이 짧은 기준을 반영해 추천된 만남 장소입니다."
                ));
    }

    @Test
    @DisplayName("추천 태그가 없으면 적당한 추천 이유를 생성한다")
    void createReasons_UsesGeneralReasonWhenTagsAreMissing() {
        final Place cityHall = place("시청역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(cityHall),
                Map.of()
        );

        final Map<Place, RecommendationReason> result = recommendedCandidates.createReasons();

        assertThat(result)
                .containsEntry(cityHall, new RecommendationReason(
                        "#적당한",
                        "시청역은 이동 시간, 환승, 균형이 적당한 기준을 반영해 추천된 만남 장소입니다."
                ));
    }

    @Test
    @DisplayName("추천 후보가 아닌 장소의 태그는 조회할 수 없다")
    void getTag_ThrowsExceptionWhenPlaceIsNotRecommendedCandidate() {
        final Place seolleung = place("선릉역");
        final Place samsung = place("삼성역");
        final RecommendedCandidates recommendedCandidates = new RecommendedCandidates(
                List.of(seolleung),
                Map.of(seolleung, CandidateSelectionTag.FAIRNESS)
        );

        assertSoftly(softAssertions -> {
            softAssertions.assertThatThrownBy(() -> recommendedCandidates.getTag(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 장소는 null일 수 없습니다.");
            softAssertions.assertThatThrownBy(() -> recommendedCandidates.getTag(samsung))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("추천 후보 태그가 누락되었습니다. 추천 지역: 삼성역");
        });
    }

    private Place place(final String name) {
        return new Place(name, new Point(127.0, 37.0));
    }

}
