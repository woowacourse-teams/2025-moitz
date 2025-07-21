/** @jsxImportSource @emotion/react */
import { typography } from '../../styles/default.styled';

import * as bottomButton from './bottomButton.styled';

interface BottomButtonProps {
  text: string;
  active: boolean;
}

function BottomButton({ text, active }: BottomButtonProps) {
  return (
    <button
      type="button"
      css={[bottomButton.base(), active && bottomButton.active()]}
    >
      <span css={typography.sh2}>{text}</span>
    </button>
  );
}

export default BottomButton;
