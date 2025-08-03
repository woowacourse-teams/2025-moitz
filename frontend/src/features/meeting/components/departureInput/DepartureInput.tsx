import React, { useState } from 'react';

import InputFormSection from '@features/meeting/components/meetingFormSection/MeetingFormSection';
import { INPUT_FORM_TEXT } from '@features/meeting/constant/inputForm';

import Input from '@shared/components/input/Input';
import Tag from '@shared/components/tag/Tag';
import { flex } from '@shared/styles/default.styled';

interface DepartureInputProps {
  departureList: string[];
  onAddDeparture: (value: string) => void;
  onRemoveDeparture: (index: number) => void;
}

function DepartureInput({
  departureList,
  onAddDeparture,
  onRemoveDeparture,
}: DepartureInputProps) {
  const [inputValue, setInputValue] = useState<string>('');

  const handleInputValue = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (e.nativeEvent.isComposing) return;

      const trimmedValue = inputValue.trim();
      if (trimmedValue !== '') {
        onAddDeparture(trimmedValue);
        setInputValue('');
      }
    }
  };

  return (
    <InputFormSection
      titleText={INPUT_FORM_TEXT.DEPARTURE.TITLE}
      descriptionText={INPUT_FORM_TEXT.DEPARTURE.DESCRIPTION}
    >
      <Input
        placeholder="출발지를 입력해주세요"
        value={inputValue}
        onChange={handleInputValue}
        onKeyDown={handleKeyDown}
      />
      {departureList.length > 0 && (
        <div css={[flex({ gap: 5 }), { flexWrap: 'wrap' }]}>
          {departureList.map((name, index) => (
            <Tag
              key={index}
              text={name}
              onClick={() => onRemoveDeparture(index)}
            />
          ))}
        </div>
      )}
    </InputFormSection>
  );
}

export default DepartureInput;
