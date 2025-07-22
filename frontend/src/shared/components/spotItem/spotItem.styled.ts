import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '../../styles/tokens';

export const base = () => css`
  width: 100%;
  padding: 10px 15px 10px 10px;
  color: ${colorToken.gray[1]};
  background-color: ${colorToken.gray[8]};
  border-radius: ${borderRadiusToken.input};

  &:hover {
    background-color: ${colorToken.main[4]};
  }
`;

export const contents_container = () => css`
  width: 100%;
`;
