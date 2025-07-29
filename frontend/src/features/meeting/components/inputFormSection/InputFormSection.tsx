/** @jsxImportSource @emotion/react */
import React from 'react';

import { flex, typography } from '@shared/styles/default.styled';

import * as inputFormSection from './inputFormSection.styled';

interface InputFormSectionProps {
  titleText: string;
  descriptionText: string;
  children: React.ReactNode;
}

function InputFormSection({
  titleText,
  descriptionText,
  children,
}: InputFormSectionProps) {
  return (
    <div css={flex({ direction: 'column', gap: 10 })}>
      <div
        css={[flex({ direction: 'column', gap: 8 }), inputFormSection.header()]}
      >
        <span css={[typography.h2, inputFormSection.title()]}>{titleText}</span>
        <span css={[typography.b2, inputFormSection.description()]}>
          {descriptionText}
        </span>
      </div>
      {children}
    </div>
  );
}

export default InputFormSection;
