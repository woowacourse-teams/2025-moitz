/** @jsxImportSource @emotion/react */
import React, { useState, useRef, useEffect } from 'react';

import { stationList } from '@features/meeting/config/stationList';

import Input from '@shared/components/input/Input';
import Tag from '@shared/components/tag/Tag';
import Toast from '@shared/components/toast/Toast';
import { useToast } from '@shared/hooks/useToast';
import { flex, typography } from '@shared/styles/default.styled';

import * as planForm from '../planForm.styled';
import PlanFormTitle from '../planFormTitle/PlanFormTitle';

import * as inputSection from './startingPlaceInputSection.styleld';

interface StartingPlaceInputSectionProps {
  startingPlaces: string[];
  setStartingPlaces: (places: string[]) => void;
}

function StartingPlaceInputSection({
  startingPlaces,
  setStartingPlaces,
}: StartingPlaceInputSectionProps) {
  const [placeInputValue, setPlaceInputValue] = useState<string>('');
  const [filteredStations, setFilteredStations] = useState<string[]>([]);
  const [showDropdown, setShowDropdown] = useState<boolean>(false);
  const { isVisible, message, showToast, hideToast } = useToast();
  const inputRef = useRef<HTMLInputElement>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // 클릭 외부 감지를 위한 useEffect
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node) &&
        inputRef.current &&
        !inputRef.current.contains(event.target as Node)
      ) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handlePlaceInputValue = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPlaceInputValue(value);

    // 입력값이 있으면 필터링하고 드롭다운 표시
    if (value.trim()) {
      const filtered = stationList
        .filter((station) => station.includes(value.trim()))
        .slice(0, 10); // 최대 10개까지만 표시
      setFilteredStations(filtered);
      setShowDropdown(true);
    } else {
      setFilteredStations([]);
      setShowDropdown(false);
    }
  };

  const handleStationSelect = (station: string) => {
    setPlaceInputValue('');
    setShowDropdown(false);

    // 중복 체크
    if (startingPlaces.includes(station)) {
      showToast('이미 출발지에 존재하는 역입니다.');
      return false;
    }

    // 최대 개수 체크
    if (startingPlaces.length >= 6) {
      showToast('최대 6개의 출발지를 입력할 수 있습니다.');
      return false;
    }

    // 출발지 추가
    setStartingPlaces([...startingPlaces, station]);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (e.nativeEvent.isComposing) return;
      if (placeInputValue.trim() !== '') {
        const trimmedValue = placeInputValue.trim();

        // 입력값이 역 목록에 있는지 확인
        const isValidStation = stationList.includes(trimmedValue);

        if (!isValidStation) {
          showToast('서울내에 존재하는 역이름을 작성해주세요');
          return;
        }

        handleStationSelect(trimmedValue);
      }
    } else if (e.key === 'Escape') {
      setShowDropdown(false);
    }
  };

  const removeTag = (indexToRemove: number) => {
    setStartingPlaces(
      startingPlaces.filter((_, index) => index !== indexToRemove),
    );
  };

  return (
    <div css={flex({ direction: 'column', gap: 5 })}>
      <PlanFormTitle text="출발지" isRequired={true} />
      <div css={inputSection.inputWrapper()}>
        <Input
          ref={inputRef}
          placeholder="출발하는 역 이름을 입력해주세요."
          value={placeInputValue}
          onChange={handlePlaceInputValue}
          onKeyDown={handleKeyDown}
          onFocus={() => {
            if (placeInputValue.trim() && filteredStations.length > 0) {
              setShowDropdown(true);
            }
          }}
        />

        {/* 드롭다운 */}
        {showDropdown && filteredStations.length > 0 && (
          <div ref={dropdownRef} css={inputSection.dropdown()}>
            {filteredStations.map((station, index) => (
              <div
                key={index}
                css={inputSection.dropdownItem(
                  index === filteredStations.length - 1,
                )}
                onClick={() => handleStationSelect(station)}
              >
                {station}
              </div>
            ))}
          </div>
        )}
      </div>

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
      <Toast isVisible={isVisible} message={message} onClose={hideToast} />
    </div>
  );
}

export default StartingPlaceInputSection;
