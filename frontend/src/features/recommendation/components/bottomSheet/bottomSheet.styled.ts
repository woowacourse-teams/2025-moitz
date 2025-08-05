import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 100%;
  height: 50%;
  padding: 20px 20px 0px 20px;
  background-color: ${colorToken.gray[8]};
  border-top-left-radius: ${borderRadiusToken.input};
  border-top-right-radius: ${borderRadiusToken.input};
`;
