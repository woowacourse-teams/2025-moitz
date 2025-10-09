import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const backdrop = () => css`
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 100;
  background-color: ${colorToken.blur};
`;

export const base = () => css`
  width: 100%;
  padding: 20px;
  background-color: ${colorToken.gray[8]};
  border-radius: ${borderRadiusToken[20]};
`;
