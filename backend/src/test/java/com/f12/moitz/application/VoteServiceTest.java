package com.f12.moitz.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.f12.moitz.application.dto.VotesResponse;
import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import com.f12.moitz.common.error.exception.NotFoundException;
import com.f12.moitz.domain.repository.RecommendResultRepository;
import com.f12.moitz.domain.repository.dto.CandidateVote;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private RecommendResultRepository recommendResultRepository;

    @Test
    @DisplayName("추천 후보에 투표하고 증가된 투표 수를 반환한다")
    void addVote() {
        final VoteService voteService = new VoteService(recommendResultRepository);
        final ObjectId id = new ObjectId();
        given(recommendResultRepository.existsById(id)).willReturn(true);
        given(recommendResultRepository.findVotesByIdAndCandidate(id, "선릉역"))
                .willReturn(Optional.of(new CandidateVote("선릉역", 3)));

        final VotesResponse response = voteService.addVote(id.toHexString(), "선릉역");

        assertThat(response.locationName()).isEqualTo("선릉역");
        assertThat(response.count()).isEqualTo(3);
        verify(recommendResultRepository).incrementVotesByIdAndCandidate(id, "선릉역");
    }

    @Test
    @DisplayName("투표할 추천 후보 이름이 비어있으면 제어된 예외를 반환한다")
    void addVote_ThrowsBadRequestWhenCandidateNameIsBlank() {
        final VoteService voteService = new VoteService(recommendResultRepository);

        assertThatThrownBy(() -> voteService.addVote(new ObjectId().toHexString(), " "))
                .isInstanceOfSatisfying(BadRequestException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_CANDIDATE_NAME));
    }

    @Test
    @DisplayName("추천 결과가 없으면 제어된 예외를 반환한다")
    void addVote_ThrowsNotFoundWhenResultDoesNotExist() {
        final VoteService voteService = new VoteService(recommendResultRepository);
        final ObjectId id = new ObjectId();
        given(recommendResultRepository.existsById(id)).willReturn(false);

        assertThatThrownBy(() -> voteService.addVote(id.toHexString(), "선릉역"))
                .isInstanceOfSatisfying(NotFoundException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_RESULT));
    }

    @Test
    @DisplayName("투표할 추천 후보가 없으면 제어된 예외를 반환한다")
    void addVote_ThrowsNotFoundWhenCandidateDoesNotExist() {
        final VoteService voteService = new VoteService(recommendResultRepository);
        final ObjectId id = new ObjectId();
        given(recommendResultRepository.existsById(id)).willReturn(true);
        given(recommendResultRepository.findVotesByIdAndCandidate(id, "없는역"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> voteService.addVote(id.toHexString(), "없는역"))
                .isInstanceOfSatisfying(NotFoundException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(GeneralErrorCode.INPUT_INVALID_CANDIDATE_NAME));
    }

    @Test
    @DisplayName("추천 결과의 전체 투표 수를 반환한다")
    void getAllVotes() {
        final VoteService voteService = new VoteService(recommendResultRepository);
        final ObjectId id = new ObjectId();
        given(recommendResultRepository.existsById(id)).willReturn(true);
        given(recommendResultRepository.findAllVotesById(id)).willReturn(List.of(
                new CandidateVote("선릉역", 3),
                new CandidateVote("삼성역", 1)
        ));

        final List<VotesResponse> responses = voteService.getAllVotes(id.toHexString());

        assertThat(responses)
                .extracting(VotesResponse::locationName)
                .containsExactly("선릉역", "삼성역");
        assertThat(responses)
                .extracting(VotesResponse::count)
                .containsExactly(3, 1);
    }

}
