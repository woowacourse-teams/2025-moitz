import { useState } from 'react';

import {
  validateDepartureListMaxLength,
  validateStationName,
  validateDuplicateDeparture,
  validateForm,
} from '@features/meeting/utils/formValidation';

import { ValidationError } from '@shared/types/validationError';

type UseFormInfoReturn = {
  departureList: string[];
  conditionID: string;
  addDepartureWithValidation: (departure: string) => ValidationError;
  removeDepartureAtIndex: (index: number) => void;
  updateConditionID: (condition: string) => void;
  validateFormSubmit: () => ValidationError;
};

export function useFormInfo(): UseFormInfoReturn {
  const [departureList, setDepartureList] = useState<string[]>([]);
  const [conditionID, setConditionID] = useState<string>('');

  const addDepartureWithValidation = (departure: string): ValidationError => {
    const stationNameValidation = validateStationName(departure);
    if (!stationNameValidation.isValid) {
      return stationNameValidation;
    }

    const duplicateValidation = validateDuplicateDeparture(
      departureList,
      departure,
    );
    if (!duplicateValidation.isValid) {
      return duplicateValidation;
    }

    const lengthValidation = validateDepartureListMaxLength(
      departureList.length,
    );
    if (!lengthValidation.isValid) {
      return lengthValidation;
    }

    setDepartureList((prev) => [...prev, departure]);
    return { isValid: true, message: '' };
  };

  const removeDepartureAtIndex = (index: number) => {
    setDepartureList((prev) => prev.filter((_, i) => i !== index));
  };

  const updateConditionID = (condition: string) => {
    setConditionID(condition);
  };

  const validateFormSubmit = (): ValidationError => {
    return validateForm(departureList, conditionID);
  };

  return {
    departureList,
    conditionID,
    addDepartureWithValidation,
    removeDepartureAtIndex,
    updateConditionID,
    validateFormSubmit,
  };
}
