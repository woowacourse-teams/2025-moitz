import React from 'react';

import { typography } from '@shared/styles/default.styled';

import * as textarea from './textarea.styled';
interface TextareaProps {
  placeholder: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
}

function Textarea({ placeholder, value, onChange }: TextareaProps) {
  return (
    <textarea
      css={[textarea.base(), typography.b1]}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
    />
  );
}

export default Textarea;
