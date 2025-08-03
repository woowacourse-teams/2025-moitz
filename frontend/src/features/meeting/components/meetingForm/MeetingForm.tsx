/** @jsxImportSource @emotion/react */
import React, { useState } from 'react';

import InputFormSection from '@features/meeting/components/meetingFormSection/MeetingFormSection';
import { CONDITION_CARD_TEXT } from '@features/meeting/constant/conditionCard';
import { INPUT_FORM_TEXT } from '@features/meeting/constant/inputForm';
import { useFormInfo } from '@features/meeting/hooks/useFormInfo';
import Toast from '@features/toast/components/Toast';
import { useToast } from '@features/toast/hooks/useToast';

import BottomButton from '@shared/components/bottomButton/BottomButton';
import Input from '@shared/components/input/Input';
import Tag from '@shared/components/tag/Tag';
import { flex } from '@shared/styles/default.styled';

import { ValidationError } from '@shared/types/validationError';

import ConditionCard from '../conditionCard/ConditionCard';

import * as meetingForm from './meetingForm.styled';

function MeetingForm() {
  const {
    departureList,
    conditionID,
    addDepartureWithValidation,
    removeDepartureAtIndex,
    updateConditionID,
    validateFormSubmit,
  } = useFormInfo();
  const { isVisible, message, showToast } = useToast();

  const [inputValue, setInputValue] = useState<string>('');

  const handleInputValue = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const showValidationError = (error: ValidationError) => {
    if (!error.isValid) {
      showToast(error.message);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (e.nativeEvent.isComposing) return;

      const trimmedValue = inputValue.trim();
      if (trimmedValue !== '') {
        const validationResult = addDepartureWithValidation(trimmedValue);
        if (!validationResult.isValid) {
          showValidationError(validationResult);
          return;
        }
        setInputValue('');
      }
    }
  };

  const handleConditionCardClick = (condition: string) => {
    updateConditionID(condition);
  };

  const validateActive = () => {
    const formValidation = validateFormSubmit();
    return formValidation.isValid;
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const formValidation = validateFormSubmit();
    if (!formValidation.isValid) {
      showValidationError(formValidation);
      return;
    }

    console.log(departureList, conditionID);
  };

  return (
    <form css={flex({ direction: 'column', gap: 80 })} onSubmit={handleSubmit}>
      <div css={flex({ direction: 'column', gap: 50 })}>
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
                  onClick={() => removeDepartureAtIndex(index)}
                />
              ))}
            </div>
          )}
        </InputFormSection>

        <InputFormSection
          titleText={INPUT_FORM_TEXT.CONDITION.TITLE}
          descriptionText={INPUT_FORM_TEXT.CONDITION.DESCRIPTION}
        >
          <div css={meetingForm.card_container()}>
            {Object.values(CONDITION_CARD_TEXT).map((condition) => (
              <ConditionCard
                key={condition.ID}
                iconText={condition.ICON}
                contentText={condition.TEXT}
                isSelected={conditionID === condition.ID}
                onClick={() => handleConditionCardClick(condition.ID)}
              />
            ))}
          </div>
        </InputFormSection>
      </div>

      <BottomButton
        type="submit"
        text="중간지점 찾기"
        active={validateActive()}
        onClick={() => {}}
      />
      <Toast message={message} isVisible={isVisible} />
    </form>
  );
}

export default MeetingForm;
