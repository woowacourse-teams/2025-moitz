/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import * as badge from './badge.styled';

interface BadgeProps {
  text: string;
}

function Badge({ text }: BadgeProps) {
  return (
    <div css={[flex({ justify: 'center', align: 'center' }), badge.base()]}>
      <span css={typography.sh2}>{text}</span>
    </div>
  );
}

export default Badge;
