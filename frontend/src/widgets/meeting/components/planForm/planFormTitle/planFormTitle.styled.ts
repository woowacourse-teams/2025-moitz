import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const container = () => css`
  padding-left: 10px;
`;

export const title = () => css`
  color: ${colorToken.gray[2]};
`;

export const required = () => css`
  color: ${colorToken.orange[1]};
`;
