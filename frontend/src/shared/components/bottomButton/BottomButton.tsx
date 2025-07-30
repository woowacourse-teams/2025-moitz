/** @jsxImportSource @emotion/react */
import { flex, typography } from '@shared/styles/default.styled';

import * as bottomButton from './bottomButton.styled';

interface BottomButtonProps {
  type: 'button' | 'submit';
  text: string;
  active: boolean;
  onClick: () => void;
}

function BottomButton({ type, text, active, onClick }: BottomButtonProps) {
  return (
    <button
      type={type}
      css={[
        flex({ justify: 'center', align: 'center' }),
        bottomButton.base(),
        active && bottomButton.active(),
      ]}
      onClick={onClick}
    >
      <span css={typography.h1}>{text}</span>
    </button>
  );
}

export default BottomButton;
