import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '../../styles/tokens';

export const base = () => css`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px 30px;
  width: 100%;
  border: none;
  cursor: pointer;
  border-radius: ${borderRadiusToken.button};
  color: ${colorToken.gray[7]};
  background-color: ${colorToken.main[3]};
`;

export const active = () => css`
  background-color: ${colorToken.main[1]};

  &:hover {
    background-color: ${colorToken.main[2]};
  }
`;
