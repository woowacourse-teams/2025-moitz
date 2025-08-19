import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const container = () => css`
  background-color: ${colorToken.bg[2]};
  border-radius: ${borderRadiusToken[10]};
  padding: 10px;
`;

export const title = () => css`
  color: ${colorToken.gray[2]};
`;

export const bar = () => css``;
