import { CONDITION_CARD_TEXT } from '@features/meeting/constants/conditionCard';
import { INPUT_FORM_TEXT } from '@features/meeting/constants/inputForm';

import ConditionCard from '../conditionCard/ConditionCard';
import InputFormSection from '../meetingFormSection/MeetingFormSection';

import * as conditionSelector from './conditionSelector.styled';

interface ConditionSelectorProps {
  selectedConditionID: string;
  onSelect: (condition: string) => void;
}

function ConditionSelector({
  selectedConditionID,
  onSelect,
}: ConditionSelectorProps) {
  const handleConditionCardClick = (condition: string) => {
    onSelect(condition);
  };

  return (
    <InputFormSection
      titleText={INPUT_FORM_TEXT.CONDITION.TITLE}
      descriptionText={INPUT_FORM_TEXT.CONDITION.DESCRIPTION}
    >
      <div css={conditionSelector.container()}>
        {Object.values(CONDITION_CARD_TEXT).map((condition) => (
          <ConditionCard
            key={condition.ID}
            iconText={condition.ICON}
            contentText={condition.TEXT}
            isSelected={selectedConditionID === condition.ID}
            onClick={() => handleConditionCardClick(condition.ID)}
          />
        ))}
      </div>
    </InputFormSection>
  );
}

export default ConditionSelector;
