/** @jsxImportSource @emotion/react */
import { flex, typography } from '@shared/styles/default.styled';

import * as planFormTitle from './planFormTitle.styled';

interface PlanFormTitleProps {
  text: string;
  isRequired: boolean;
}

function PlanFormTitle({ text, isRequired }: PlanFormTitleProps) {
  return (
    <div css={[flex({ gap: 3 }), planFormTitle.container()]}>
      <span css={[typography.h2, planFormTitle.title()]}>{text}</span>
      {isRequired && (
        <span css={[typography.h2, planFormTitle.required()]}>*</span>
      )}
    </div>
  );
}

export default PlanFormTitle;
