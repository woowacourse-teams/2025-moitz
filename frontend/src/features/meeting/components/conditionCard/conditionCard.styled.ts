import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 12px;
  border: none;
  cursor: pointer;
  background-color: ${colorToken.gray[8]};
  border-radius: ${borderRadiusToken[10]};

  &:hover {
    background-color: ${colorToken.main[4]};
  }
`;

export const text = () => css`
  color: ${colorToken.gray[2]};
`;

export const selected = () => css`
  background-color: ${colorToken.main[4]};
  box-shadow: 0 0 0 2px ${colorToken.main[1]};
`;
