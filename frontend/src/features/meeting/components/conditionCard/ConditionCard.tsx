/** @jsxImportSource @emotion/react */
import { flex, typography } from '@shared/styles/default.styled';

import * as conditionCard from './conditionCard.styled';

interface ConditionCardProps {
  iconText: string;
  contentText: string;
  onClick: () => void;
}

function ConditionCard({ iconText, contentText, onClick }: ConditionCardProps) {
  return (
    <button
      css={[
        flex({ direction: 'column', align: 'center', gap: 5 }),
        conditionCard.base(),
      ]}
      type="button"
      onClick={onClick}
    >
      <div css={[typography.b1, conditionCard.text()]}>{iconText}</div>
      <div css={[typography.b1, conditionCard.text()]}>{contentText}</div>
    </button>
  );
}

export default ConditionCard;
