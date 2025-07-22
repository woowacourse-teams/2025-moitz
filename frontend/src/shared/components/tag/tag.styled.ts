import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  padding: 5px 5px 5px 10px;
  border-radius: ${borderRadiusToken.global};
  border: 1px solid ${colorToken.main[1]};
  color: ${colorToken.sub[1]};
  background-color: ${colorToken.sub[2]};
`;

export const button = () => css`
  border: none;
  cursor: pointer;
  background-color: transparent;
`;
