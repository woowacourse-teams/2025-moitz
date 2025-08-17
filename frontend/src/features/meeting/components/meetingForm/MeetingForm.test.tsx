import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import MeetingForm from './MeetingForm';

describe('MeetingForm 통합 테스트', () => {
  const renderMeetingForm = () => {
    return render(<MeetingForm />);
  };

  describe('DepartureInput 입력', () => {
    it('출발지를 입력하면 tag 컴포넌트가 생성된다.', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 출발지를 입력하고 엔터를 누르면
      await userEvent.type(input, '강남역');
      await userEvent.keyboard('{Enter}');

      // Then: 태그가 생성된다
      const tag = screen.getByText('강남역');
      expect(tag).toBeInTheDocument();
    });

    it('올바르지 않은 출발지를 입력하면 토스트 메세지가 발생한다', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 잘못된 출발지를 입력하고 엔터를 누르면
      await userEvent.type(input, '잘못된역이름');
      await userEvent.keyboard('{Enter}');

      // Then: 에러 메시지가 토스트로 표시된다
      await waitFor(() => {
        expect(
          screen.getByText('서울 내의 올바른 지하철 역이름을 입력해주세요'),
        ).toBeInTheDocument();
      });
    });

    it('중복된 출발지를 입력하면 토스트 메세지가 발생한다', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 동일한 출발지를 두 번 입력하면
      const stations = ['강남역', '강남역'];
      for (const station of stations) {
        await userEvent.clear(input);
        await userEvent.type(input, station);
        await userEvent.keyboard('{Enter}');
      }

      // Then: 중복 입력 에러 메시지가 토스트로 표시된다
      await waitFor(() => {
        expect(screen.getByText('이미 추가된 출발지예요')).toBeInTheDocument();
      });
    });

    it('6개 이상의 출발지를 입력하면 토스트 메세지가 발생한다', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 6개의 출발지를 입력하고 추가로 하나 더 입력하면
      const stations = [
        '강남역',
        '역삼역',
        '선릉역',
        '삼성중앙역',
        '종각역',
        '시청역',
        '강변역',
      ];
      for (const station of stations) {
        await userEvent.clear(input);
        await userEvent.type(input, station);
        await userEvent.keyboard('{Enter}');
      }

      // Then: 최대 개수 초과 에러 메시지가 토스트로 표시된다
      await waitFor(() => {
        expect(
          screen.getByText('출발지는 최대 6개까지 추가할 수 있어요'),
        ).toBeInTheDocument();
      });
    });
  });

  describe('meetingForm 제출', () => {
    it('출발지 2개 이상 선택하고 ConditionSelector 선택시 BottomButton이 활성화된다', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 2개의 출발지를 입력하고 조건을 선택하면
      const stations = ['강남역', '역삼역'];
      for (const station of stations) {
        await userEvent.clear(input);
        await userEvent.type(input, station);
        await userEvent.keyboard('{Enter}');
      }
      const conditionButton = screen.getByText('떠들고 놀기 좋은');
      await userEvent.click(conditionButton);

      // Then: 제출 버튼이 활성화된다
      const submitButton = screen.getByText('중간지점 찾기');
      expect(submitButton).not.toBeDisabled();
    });

    it('출발지 2개 이하 입력시 BottomButton이 활성화되지 않는다', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 1개의 출발지만 입력하면
      await userEvent.type(input, '강남역');
      await userEvent.keyboard('{Enter}');

      // Then: 제출 버튼이 비활성화된다
      const submitButton = screen.getByText('중간지점 찾기');
      expect(submitButton).toBeEnabled();
    });

    it('조건을 선택하지 않으면 BottomButton이 활성화되지 않는다', async () => {
      // Given: MeetingForm이 렌더링되어 있고
      renderMeetingForm();
      const input = screen.getByPlaceholderText('출발지를 입력해주세요');

      // When: 2개의 출발지를 입력하고 조건을 선택하지 않으면
      const stations = ['강남역', '역삼역'];
      for (const station of stations) {
        await userEvent.clear(input);
        await userEvent.type(input, station);
        await userEvent.keyboard('{Enter}');
      }

      // Then: 제출 버튼이 비활성화된다
      const submitButton = screen.getByText('중간지점 찾기');
      expect(submitButton).toBeEnabled();
    });
  });
});
