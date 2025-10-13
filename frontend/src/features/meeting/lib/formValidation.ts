import { ValidationError } from '@shared/types/validationError';

import { STATION_LIST } from '../constants/stationList';

const validateDepartureListMinLength = (
  length: number,
  minLength: number = 2,
): ValidationError => {
  if (length < minLength) {
    return {
      isValid: false,
      message: '최소 2개 이상의 출발지를 입력해주세요',
    };
  }
  return { isValid: true, message: '' };
};

export const validateDepartureListMaxLength = (
  length: number,
  maxLength: number = 6,
): ValidationError => {
  if (length >= maxLength) {
    return {
      isValid: false,
      message: '출발지는 최대 6개까지 추가할 수 있어요',
    };
  }
  return { isValid: true, message: '' };
};

export const validateStationName = (name: string): ValidationError => {
  const stationName = name.trim();

  const exactMatch = STATION_LIST.find((station) => station === stationName);
  if (exactMatch) {
    return { isValid: true, message: '' };
  }

  const partialMatch = STATION_LIST.filter((station) =>
    station.includes(stationName),
  );

  if (partialMatch.length === 0) {
    return {
      isValid: false,
      message: '서울 내의 올바른 지하철 역이름을 입력해주세요',
    };
  }

  if (partialMatch.length > 1) {
    return {
      isValid: false,
      message: '여러 출발지가 검색되었어요. 하나만 선택해주세요.',
    };
  }

  return { isValid: true, message: '' };
};

export const validateDuplicateDeparture = (
  departureList: string[],
  newStation: string,
): ValidationError => {
  if (departureList.includes(newStation)) {
    return {
      isValid: false,
      message: '이미 추가된 출발지예요',
    };
  }
  return { isValid: true, message: '' };
};

export const validateForm = (
  departureList: string[],
  conditionId: string,
): ValidationError => {
  const minLengthValidation = validateDepartureListMinLength(
    departureList.length,
  );
  if (!minLengthValidation.isValid) {
    return minLengthValidation;
  }

  if (!conditionId) {
    return {
      isValid: false,
      message: '조건을 선택해주세요',
    };
  }

  return { isValid: true, message: '' };
};
