import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const container = () => css`
  width: 100%;
  height: 50vh;
  min-height: 50vh;
  padding: 20px 20px 0px 20px;
  background-color: ${colorToken.gray[8]};
  border-top-left-radius: ${borderRadiusToken[10]};
  border-top-right-radius: ${borderRadiusToken[10]};
`;

export const content = () => css`
  padding-bottom: 20px;
`;
