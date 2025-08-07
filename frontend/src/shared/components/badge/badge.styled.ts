import { css } from '@emotion/react';

import { typography } from '@shared/styles/default.styled';
import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

import { BadgeType } from './Badge';

type BadgeStyle = {
  padding: string;
  color: string;
  backgroundColor: string;
  typography: ReturnType<typeof css>;
};

const BADGE_STYLES: Record<BadgeType, BadgeStyle> = {
  best: {
    padding: '3px 10px',
    color: colorToken.gray[8],
    backgroundColor: colorToken.orange[2],
    typography: typography.sh2,
  },
  category: {
    padding: '3px 6px',
    color: colorToken.gray[2],
    backgroundColor: colorToken.bg[2],
    typography: typography.c2,
  },
  transfer: {
    padding: '5px',
    color: colorToken.sub[1],
    backgroundColor: colorToken.gray[7],
    typography: typography.sh3,
  },
};

export const base = (type: BadgeType) => css`
  padding: ${BADGE_STYLES[type].padding};
  border-radius: ${borderRadiusToken.global};
  color: ${BADGE_STYLES[type].color};
  background-color: ${BADGE_STYLES[type].backgroundColor};
`;

export const text = (type: BadgeType) => css`
  ${BADGE_STYLES[type].typography};
`;
