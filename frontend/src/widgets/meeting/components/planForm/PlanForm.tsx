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
import Tag from '@shared/components/tag/Tag';
import Textarea from '@shared/components/textarea/Textarea';
import { flex, typography } from '@shared/styles/default.styled';

import * as planForm from './planForm.styled';
import PlanFormTitle from './planFormTitle/PlanFormTitle';

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

  const [placeInputValue, setPlaceInputValue] = useState<string>('');
  const [timeInputValue, setTimeInputValue] = useState<string>('');
  const [isTimeModalOpen, setIsTimeModalOpen] = useState<boolean>(false);

  const handlePlaceInputValue = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPlaceInputValue(e.target.value);
  };

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

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (e.nativeEvent.isComposing) {
        return;
      }
      if (placeInputValue.trim() !== '') {
        setStartingPlaces([...startingPlaces, placeInputValue.trim()]);
        setPlaceInputValue('');
      }
    }
  };

  const removeTag = (indexToRemove: number) => {
    setStartingPlaces(
      startingPlaces.filter((_, index) => index !== indexToRemove),
    );
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (startingPlaces.length < 2) {
      alert('최소 2개 이상의 출발지를 입력해주세요');
      return;
    }

    // startingPlaces를 원하는 형태로 변환
    const formattedStartingPlaces = startingPlaces.map((place, index) => ({
      index,
      name: place,
    }));

    console.log(meetingTime);

    navigate('/result', {
      state: {
        startingPlaces: formattedStartingPlaces,
      },
    });
  };

  return (
    <form css={flex({ direction: 'column', gap: 50 })} onSubmit={handleSubmit}>
      {/* 출발지 입력 */}
      <div css={flex({ direction: 'column', gap: 5 })}>
        <PlanFormTitle text="출발지" isRequired={true} />
        <Input
          placeholder="출발하는 역 이름을 입력해주세요."
          value={placeInputValue}
          onChange={handlePlaceInputValue}
          onKeyDown={handleKeyDown}
        />
        {startingPlaces.length > 0 && (
          <div css={[flex({ gap: 5 }), { flexWrap: 'wrap' }]}>
            {startingPlaces.map((tag, index) => (
              <Tag key={index} text={tag} onClick={() => removeTag(index)} />
            ))}
          </div>
        )}
        {startingPlaces.length === 0 && (
          <p css={[typography.b2, planForm.description()]}>
            최소 2개 이상의 출발지를 입력해주세요
          </p>
        )}
      </div>

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
    </form>
  );
}

export default PlanForm;
