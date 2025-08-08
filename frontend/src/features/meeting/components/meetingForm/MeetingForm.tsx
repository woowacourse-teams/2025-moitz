import React from 'react';
import { useNavigate } from 'react-router';

import { useFormInfo } from '@features/meeting/hooks/useFormInfo';
import Toast from '@features/toast/components/Toast';
import { useToast } from '@features/toast/hooks/useToast';

import { useLocationsContext } from '@entities/contexts/useLocationsContext';

import BottomButton from '@shared/components/bottomButton/BottomButton';
import { flex } from '@shared/styles/default.styled';

import { ValidationError } from '@shared/types/validationError';

import ConditionSelector from '../conditionSelector/ConditionSelector';
import DepartureInput from '../departureInput/DepartureInput';

function MeetingForm() {
  let navigate = useNavigate();

  const {
    departureList,
    conditionID,
    addDepartureWithValidation,
    removeDepartureAtIndex,
    updateConditionID,
    validateFormSubmit,
  } = useFormInfo();
  const { isVisible, message, showToast } = useToast();

  const { trigger } = useLocationsContext();

  const showValidationError = (error: ValidationError) => {
    if (!error.isValid) {
      showToast(error.message);
    }
  };

  const handleAddDeparture = (value: string) => {
    const validationResult = addDepartureWithValidation(value);
    if (!validationResult.isValid) {
      showValidationError(validationResult);
      return;
    }
  };

  const validateActive = () => {
    const formValidation = validateFormSubmit();
    return formValidation.isValid;
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const formValidation = validateFormSubmit();
    if (!formValidation.isValid) {
      showValidationError(formValidation);
      return;
    }

    await trigger({
      startingPlaceNames: departureList,
      requirement: conditionID,
    });

    navigate('/result');
  };

  return (
    <form css={flex({ direction: 'column', gap: 80 })} onSubmit={handleSubmit}>
      <div css={flex({ direction: 'column', gap: 50 })}>
        <DepartureInput
          departureList={departureList}
          onAddDeparture={handleAddDeparture}
          onRemoveDeparture={removeDepartureAtIndex}
        />

        <ConditionSelector
          selectedConditionID={conditionID}
          onSelect={updateConditionID}
        />
      </div>

      <BottomButton
        type="submit"
        text="중간지점 찾기"
        active={validateActive()}
      />
      <Toast message={message} isVisible={isVisible} />
    </form>
  );
}

export default MeetingForm;
