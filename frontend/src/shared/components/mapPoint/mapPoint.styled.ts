import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 5px 10px;
  border-radius: ${borderRadiusToken[20]};
  color: ${colorToken.gray[1]};
  background-color: ${colorToken.gray[8]};
`;

export const dot = () => css`
  width: 8px;
  min-width: 8px;
  height: 8px;
  border-radius: ${borderRadiusToken[100]};
  background-color: ${colorToken.main[1]};
`;

export const floating = () => css`
  position: fixed;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
`;
