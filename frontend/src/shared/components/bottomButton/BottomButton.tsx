import { flex, typography } from '@shared/styles/default.styled';

import * as bottomButton from './bottomButton.styled';

interface BaseBottomButtonProps {
  text: string;
  active: boolean;
}

interface SubmitBottomButtonProps extends BaseBottomButtonProps {
  type: 'submit';
  onClick?: () => void;
}

interface ButtonBottomButtonProps extends BaseBottomButtonProps {
  type: 'button';
  onClick: () => void;
}

type BottomButtonProps = SubmitBottomButtonProps | ButtonBottomButtonProps;

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
