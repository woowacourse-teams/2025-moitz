/** @jsxImportSource @emotion/react */
import { useState, useEffect } from 'react';

import {
  convert24to12,
  convert12to24,
  format24Hour,
  parseDisplayTimeTo24,
  Time24Hour,
} from '@features/meeting/lib/timeUtils';

import BottomModal from '@shared/components/BottomModal/BottomModal';

import * as timePickBottomModal from './timePickBottomModal.styled';
import WheelPicker from './wheelPicker/WheelPicker';

interface TimePickBottomModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (time: string) => void;
  initialTime?: string;
}

function TimePickBottomModal({
  isOpen,
  onClose,
  onConfirm,
  initialTime,
}: TimePickBottomModalProps) {
  const [hour, setHour] = useState(0); // 0-23 (24시간 형태)
  const [minute, setMinute] = useState(0); // 0-5 (0,10,20,30,40,50분 인덱스)

  // UI 표시용 옵션들
  const meridiemOptions = ['오전', '오후'];
  const hourOptions = Array.from({ length: 12 }, (_, i) =>
    (i + 1).toString().padStart(2, '0'),
  );
  const minuteOptions = Array.from({ length: 6 }, (_, i) =>
    (i * 10).toString().padStart(2, '0'),
  );

  // 현재 시간을 12시간 형태로 변환
  const time24: Time24Hour = { hour, minute: minute * 10 };
  const time12 = convert24to12(time24);
  const meridiemIndex = time12.meridiem === '오전' ? 0 : 1;
  const hour12Index = time12.hour - 1; // 1-12를 0-11로 변환

  const handleMeridiemSelect = (index: number) => {
    const newMeridiem = index === 0 ? '오전' : '오후';
    const currentTime12 = {
      ...time12,
      meridiem: newMeridiem as '오전' | '오후',
    };
    const newTime24 = convert12to24(currentTime12);
    setHour(newTime24.hour);
  };

  const handleHourSelect = (index: number) => {
    const newHour12 = index + 1; // 0-11을 1-12로 변환
    const currentTime12 = { ...time12, hour: newHour12 };
    const newTime24 = convert12to24(currentTime12);
    setHour(newTime24.hour);
  };

  useEffect(() => {
    if (initialTime && isOpen) {
      const parsed = parseDisplayTimeTo24(initialTime);
      if (parsed) {
        setHour(parsed.hour);
        setMinute(Math.floor(parsed.minute / 10)); // 분을 인덱스로 변환
      }
    }
  }, [initialTime, isOpen]);

  const handleConfirm = () => {
    const time24: Time24Hour = { hour, minute: minute * 10 };
    const selectedTime = format24Hour(time24);
    onConfirm(selectedTime);
  };

  return (
    <BottomModal
      isOpen={isOpen}
      onClose={onClose}
      title="만남 시간 선택"
      onConfirm={handleConfirm}
    >
      <div css={timePickBottomModal.wheelArea()}>
        <div css={timePickBottomModal.wheelContainer()}>
          <WheelPicker
            options={meridiemOptions}
            selectedIndex={meridiemIndex}
            onSelect={handleMeridiemSelect}
          />
        </div>

        <div css={timePickBottomModal.wheelContainer()}>
          <WheelPicker
            options={hourOptions}
            selectedIndex={hour12Index}
            onSelect={handleHourSelect}
          />
        </div>

        <div css={timePickBottomModal.wheelContainer()}>
          <WheelPicker
            options={minuteOptions}
            selectedIndex={minute}
            onSelect={setMinute}
          />
        </div>
      </div>
    </BottomModal>
  );
}

export default TimePickBottomModal;
