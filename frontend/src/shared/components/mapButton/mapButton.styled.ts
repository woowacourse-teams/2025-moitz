import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: ${borderRadiusToken[100]};
  background-color: ${colorToken.gray[8]};
`;
