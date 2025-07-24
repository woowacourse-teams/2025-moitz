/** @jsxImportSource @emotion/react */
import React from 'react';

import { typography } from '@shared/styles/default.styled';

import * as input from './input.styled';

interface InputProps {
  placeholder: string;
  value: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  onClick?: () => void;
  readOnly?: boolean;
}

function Input({
  placeholder,
  value,
  onChange,
  onKeyDown,
  onClick,
  readOnly = false,
}: InputProps) {
  return (
    <input
      css={[input.base(), typography.b1]}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      onKeyDown={onKeyDown}
      onClick={onClick}
      readOnly={readOnly}
    />
  );
}

export default Input;
