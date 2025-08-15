import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 40px 20px;
`;

export const button = () => css`
  padding: 16px 30px;
  width: 100%;
  border: none;
  cursor: pointer;
  border-radius: ${borderRadiusToken.button};
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.main[1]};

  &:hover {
    background-color: ${colorToken.main[2]};
  }
`;
