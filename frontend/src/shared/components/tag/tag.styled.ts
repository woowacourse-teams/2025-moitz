import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  display: inline-flex;
  justify-content: center;
  align-items: center;
  padding: 5px 5px 5px 10px;
  gap: 4px;
  border-radius: ${borderRadiusToken.global};
  border: 1px solid ${colorToken.main[1]};
  color: ${colorToken.sub[1]};
  background-color: ${colorToken.sub[2]};
`;

export const button = () => css`
  display: flex;
  justify-content: center;
  align-items: center;
  border: none;
  cursor: pointer;
  background-color: transparent;
`;
