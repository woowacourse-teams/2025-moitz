import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const container = () => css`
  width: 200px;
  height: 10px;
  overflow: hidden;
  background-color: ${colorToken.gray[7]};
  border-radius: ${borderRadiusToken[10]};
`;

export const bar = (progress: number) => css`
  width: ${progress}%;
  height: 100%;
  background-color: ${colorToken.main[1]};
  border-radius: ${borderRadiusToken[10]};
`;
