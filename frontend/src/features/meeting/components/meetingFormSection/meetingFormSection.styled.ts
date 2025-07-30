import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const header = () => css`
  padding-left: 10px;
`;

export const title = () => css`
  color: ${colorToken.gray[2]};
`;

export const description = () => css`
  color: ${colorToken.gray[5]};
`;
