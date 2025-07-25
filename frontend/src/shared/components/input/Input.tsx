/** @jsxImportSource @emotion/react */
import { typography } from '@shared/styles/default.styled';

import * as input from './input.styled';

interface InputProps {
  placeholder: string;
}

function Input({ placeholder }: InputProps) {
  return (
    <input css={[input.base(), typography.b1]} placeholder={placeholder} />
  );
}

export default Input;
