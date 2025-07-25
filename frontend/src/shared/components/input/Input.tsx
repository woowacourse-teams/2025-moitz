/** @jsxImportSource @emotion/react */
import React, { forwardRef } from 'react';

import { typography } from '@shared/styles/default.styled';

import * as input from './input.styled';

interface InputProps {
  placeholder: string;
  value: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onKeyDown?: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  onClick?: () => void;
  onFocus?: () => void;
  readOnly?: boolean;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      placeholder,
      value,
      onChange,
      onKeyDown,
      onClick,
      onFocus,
      readOnly = false,
    },
    ref,
  ) => {
    return (
      <input
        ref={ref}
        css={[input.base(), typography.b1]}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onKeyDown={onKeyDown}
        onClick={onClick}
        onFocus={onFocus}
        readOnly={readOnly}
      />
    );
  },
);

Input.displayName = 'Input';

export default Input;
