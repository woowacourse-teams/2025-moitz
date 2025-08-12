import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = (
  size: number,
  colorType: keyof typeof colorToken,
  colorTokenIndex: number,
) => css`
  width: ${size}px;
  height: ${size}px;
  border-radius: ${borderRadiusToken.round};
  background-color: ${colorToken[colorType][colorTokenIndex]};
`;
