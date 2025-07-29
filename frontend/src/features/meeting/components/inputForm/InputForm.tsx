/** @jsxImportSource @emotion/react */
import React, { useState } from 'react';

import InputFormSection from '@features/meeting/components/inputFormSection/InputFormSection';
import { CONDITION_CARD_TEXT } from '@features/meeting/constant/conditionCard';
import { INPUT_FORM_TEXT } from '@features/meeting/constant/inputForm';

import BottomButton from '@shared/components/bottomButton/BottomButton';
import Input from '@shared/components/input/Input';
import { flex } from '@shared/styles/default.styled';

import ConditionCard from '../conditionCard/ConditionCard';

import * as inputForm from './inputForm.styled';

function InputForm() {
  const [inputValue, setInputValue] = useState<string>('');

  const handleInputValue = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  return (
    <div css={flex({ direction: 'column', gap: 80 })}>
      <div css={flex({ direction: 'column', gap: 50 })}>
        <InputFormSection
          titleText={INPUT_FORM_TEXT.DEPARTURE.TITLE}
          descriptionText={INPUT_FORM_TEXT.DEPARTURE.DESCRIPTION}
        >
          <Input
            placeholder="출발지를 입력해주세요"
            value={inputValue}
            onChange={handleInputValue}
            onKeyDown={() => {}}
            onClick={() => {}}
          />
        </InputFormSection>

        <InputFormSection
          titleText={INPUT_FORM_TEXT.CONDITION.TITLE}
          descriptionText={INPUT_FORM_TEXT.CONDITION.DESCRIPTION}
        >
          <div css={inputForm.card_container()}>
            {Object.values(CONDITION_CARD_TEXT).map((condition) => (
              <ConditionCard
                key={condition.ID}
                iconText={condition.ICON}
                contentText={condition.TEXT}
                onClick={() => {}}
              />
            ))}
          </div>
        </InputFormSection>
      </div>

      <BottomButton text="중간지점 찾기" active={false} onClick={() => {}} />
    </div>
  );
}

export default InputForm;
