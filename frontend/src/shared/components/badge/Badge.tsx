/** @jsxImportSource @emotion/react */
import { typography } from '../../styles/default.styled';

import * as badge from './badge.styled';

interface BadgeProps {
  text: string;
}

function Badge({ text }: BadgeProps) {
  return (
    <div css={badge.base()}>
      <span css={typography.sh2}>{text}</span>
    </div>
  );
}

export default Badge;
