import { LocationRequirement } from '@entities/location/types/LocationRequirement';

type ConditionCardText = Record<
  LocationRequirement,
  {
    ID: LocationRequirement;
    ICON: string;
    TEXT: string;
  }
>;

export const CONDITION_CARD_TEXT: ConditionCardText = {
  CHAT: {
    ID: 'CHAT',
    ICON: '💬',
    TEXT: '떠들고 놀기 좋은',
  },
  MEETING: {
    ID: 'MEETING',
    ICON: '🎤',
    TEXT: '회의하기 좋은',
  },
  FOCUS: {
    ID: 'FOCUS',
    ICON: '📖',
    TEXT: '집중하기 좋은',
  },
  DATE: {
    ID: 'DATE',
    ICON: '💝',
    TEXT: '데이트하기 좋은',
  },
  NOT_SELECTED: {
    ID: 'NOT_SELECTED',
    ICON: '✅',
    TEXT: '선택하지 않음',
  },
};
