import { flex } from '@shared/styles/default.styled';

import * as badge from './badge.styled';

export type BadgeType = 'best' | 'category' | 'transfer';

interface BadgeProps {
  type: BadgeType;
  text: string;
}

function Badge({ type, text }: BadgeProps) {
  return (
    <div css={[flex({ justify: 'center', align: 'center' }), badge.base(type)]}>
      <span css={badge.text(type)}>{text}</span>
    </div>
  );
}

export default Badge;
