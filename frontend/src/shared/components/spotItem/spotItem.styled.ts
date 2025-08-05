import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 100%;
  padding: 10px 15px 10px 10px;
  color: ${colorToken.gray[1]};
  background-color: ${colorToken.gray[8]};
  border-radius: ${borderRadiusToken.input};
  cursor: pointer;

  &:hover {
    background-color: ${colorToken.main[4]};
  }
`;

export const contents_container = () => css`
  width: 100%;
`;

export const description = () => css`
  display: -webkit-box;
  -webkit-line-clamp: 2; // TODO : 한줄? 두줄?
  -webkit-box-orient: vertical;
  overflow: hidden;
`;
