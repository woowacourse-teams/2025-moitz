import React, { useState } from 'react';

import InputFormSection from '@features/meeting/components/meetingFormSection/MeetingFormSection';
import { STATION_LIST } from '@features/meeting/config/stationList';
import { INPUT_FORM_TEXT } from '@features/meeting/constant/inputForm';

import Input from '@shared/components/input/Input';
import Tag from '@shared/components/tag/Tag';
import { flex, typography } from '@shared/styles/default.styled';

import * as input from './departureInput.styled';

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
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const filteredStations = STATION_LIST.filter((station) =>
    station.includes(inputValue.trim()),
  );

  const handleInputValue = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    setInputValue(newValue);
    setIsDropdownOpen(newValue.trim() !== '');
  };

  const handleStationSelect = (station: string) => {
    onAddDeparture(station);
    setInputValue('');
    setIsDropdownOpen(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (e.nativeEvent.isComposing) return;

      const trimmedValue = inputValue.trim();
      if (trimmedValue !== '') {
        handleStationSelect(trimmedValue);
      }
    }
  };

  return (
    <InputFormSection
      titleText={INPUT_FORM_TEXT.DEPARTURE.TITLE}
      descriptionText={INPUT_FORM_TEXT.DEPARTURE.DESCRIPTION}
    >
      <div css={input.container()}>
        <Input
          placeholder="출발지를 입력해주세요"
          value={inputValue}
          onChange={handleInputValue}
          onKeyDown={handleKeyDown}
        />
        {isDropdownOpen && inputValue.trim() && (
          <ul css={input.dropdown()}>
            {filteredStations.length > 0 ? (
              filteredStations.map((station) => (
                <li
                  key={station}
                  css={[typography.b1, input.item()]}
                  onClick={() => handleStationSelect(station)}
                >
                  {station}
                </li>
              ))
            ) : (
              <li css={[typography.b1, input.blank_item()]}>
                해당하는 역이름이 없습니다.
              </li>
            )}
          </ul>
        )}
      </div>

      <div css={[flex({ gap: 5, wrap: 'wrap' })]}>
        {departureList.map((name, index) => (
          <Tag
            key={index}
            text={name}
            onClick={() => onRemoveDeparture(index)}
          />
        ))}
      </div>
    </InputFormSection>
  );
}

export default DepartureInput;
