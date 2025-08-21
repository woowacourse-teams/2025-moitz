import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  padding: 16px 30px;
  width: 100%;
  border: none;
  cursor: pointer;
  border-radius: ${borderRadiusToken[14]};
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.main[3]};
`;

export const active = () => css`
  background-color: ${colorToken.main[1]};

  &:hover {
    background-color: ${colorToken.main[2]};
  }
`;
