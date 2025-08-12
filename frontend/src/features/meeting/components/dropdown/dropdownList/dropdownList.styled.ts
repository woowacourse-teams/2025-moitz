import { css } from '@emotion/react';

import { colorToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 12px;
  cursor: pointer;
  color: ${colorToken.gray[2]};

  &:hover {
    background-color: ${colorToken.bg[2]};
  }
`;
