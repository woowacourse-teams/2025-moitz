import React from 'react';

import Badge from '@shared/components/badge/Badge';
import { flex, typography } from '@shared/styles/default.styled';

import * as detailSection from './detailSection.styled';

interface DetailSectionProps {
  isHeader: boolean;
  title: string;
  isBestBadge?: boolean;
  children: React.ReactNode;
}

function DetailSection({
  isHeader,
  title,
  isBestBadge = false,
  children,
}: DetailSectionProps) {
  return (
    <div css={flex({ direction: 'column', gap: 10 })}>
      <div css={flex({ justify: 'space-between', align: 'center' })}>
        <span
          css={[
            isHeader ? typography.h1 : typography.h3,
            detailSection.title(),
          ]}
        >
          {title}
        </span>
        {isBestBadge && <Badge type="best" text="best" />}
      </div>
      {children}
    </div>
  );
}

export default DetailSection;
