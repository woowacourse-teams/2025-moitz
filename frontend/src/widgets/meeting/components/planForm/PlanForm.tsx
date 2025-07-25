/** @jsxImportSource @emotion/react */
import React, { useState } from 'react';
import { useNavigate } from 'react-router';

import TimePickBottomModal from '@widgets/meeting/components/timePickBottomModal/TimePickBottomModal';

import useMeetingInfo from '@features/meeting/hooks/useMeetingInfo';
import {
  parse24Hour,
  format24ToDisplay,
} from '@features/meeting/lib/timeUtils';

import BottomButton from '@shared/components/bottomButton/BottomButton';
import Input from '@shared/components/input/Input';
import Textarea from '@shared/components/textarea/Textarea';
import Toast from '@shared/components/toast/Toast';
import { useToast } from '@shared/hooks/useToast';
import { flex } from '@shared/styles/default.styled';

import PlanFormTitle from './planFormTitle/PlanFormTitle';
import StartingPlaceInputSection from './startingPlaceInputSection/StartingPlaceInputSection';

function PlanForm() {
  const navigate = useNavigate();
  const {
    startingPlaces,
    meetingTime,
    requirement,
    setStartingPlaces,
    setMeetingTime,
    setRequirement,
  } = useMeetingInfo();

  const [timeInputValue, setTimeInputValue] = useState<string>('');
  const [isTimeModalOpen, setIsTimeModalOpen] = useState<boolean>(false);
  const { isVisible, message, showToast, hideToast } = useToast();

  const handleTimeSelect = () => {
    setIsTimeModalOpen(true);
  };

  const handleTimeConfirm = (selectedTime: string) => {
    setMeetingTime(selectedTime);
    const parsed = parse24Hour(selectedTime);
    if (parsed) {
      const displayTime = format24ToDisplay(parsed);
      setTimeInputValue(displayTime);
    }
    setIsTimeModalOpen(false);
  };

  const handleTimeModalClose = () => {
    setIsTimeModalOpen(false);
  };

  const handleRequirementInputValue = (
    e: React.ChangeEvent<HTMLTextAreaElement>,
  ) => {
    setRequirement(e.target.value);
  };

  // ✅ 수정된 handleSubmit 함수
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (startingPlaces.length < 2) {
      showToast('최소 2개 이상의 출발지를 입력해주세요');
      return;
    }

    const formattedStartingPlaces = startingPlaces.map((place, index) => ({
      index,
      name: place,
    }));

    try {
      const response = await fetch(`https://dev.api.moitz.kr/locations`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          startingPlaceNames: startingPlaces,
          meetingTime,
          requirement,
        }),
      });

      if (!response.ok) {
        throw new Error('추천 장소 요청 실패');
      }

      const result = await response.json();
      console.log(result, '응답확인'); // ✅ 응답 확인

      navigate('/result', {
        state: {
          startingPlaces: formattedStartingPlaces, // ✅ 요청한 포맷 유지
          recommendedLocations: result, // ✅ 추천 장소 정보
        },
      });
    } catch (error) {
      console.error(error);
      showToast('추천 장소를 불러오는 데 실패했습니다.');
    }
  };

  return (
    <form css={flex({ direction: 'column', gap: 50 })} onSubmit={handleSubmit}>
      {/* 출발지 입력 */}
      <StartingPlaceInputSection
        startingPlaces={startingPlaces}
        setStartingPlaces={setStartingPlaces}
      />

      {/* 도착 시간 입력 */}
      <div css={flex({ direction: 'column', gap: 5 })}>
        <PlanFormTitle text="만남 시간" isRequired={false} />
        <Input
          placeholder="모임의 만남 시간을 선택하세요"
          value={timeInputValue}
          onClick={handleTimeSelect}
          readOnly={true}
        />
      </div>

      {/* 장소 추천 조건 입력 */}
      <div css={flex({ direction: 'column', gap: 5 })}>
        <PlanFormTitle text="장소 추천 조건 입력" isRequired={false} />
        <Textarea
          placeholder="예 : '조용한 카페가 5곳 이상 있어야 해요.', 'PC방이 적어도 하나 이상 있어야 해요.'"
          value={requirement}
          onChange={handleRequirementInputValue}
        />
      </div>

      <BottomButton
        type="submit"
        text="중간 지점 찾기"
        active={startingPlaces.length >= 2}
      />

      <TimePickBottomModal
        isOpen={isTimeModalOpen}
        onClose={handleTimeModalClose}
        onConfirm={handleTimeConfirm}
        initialTime={timeInputValue}
      />
      <Toast isVisible={isVisible} message={message} onClose={hideToast} />
    </form>
  );
}

export default PlanForm;
