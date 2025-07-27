import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 100%;
  padding: 10px 10px 10px 14px;
  background-color: ${colorToken.bg[2]};
  border-radius: ${borderRadiusToken.input};
`;

export const title = () => css`
  color: ${colorToken.gray[1]};
  white-space: nowrap;
`;
