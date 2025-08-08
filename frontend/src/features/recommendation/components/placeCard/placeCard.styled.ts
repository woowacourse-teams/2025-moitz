import { css } from '@emotion/react';

import { borderRadiusToken, colorToken } from '@shared/styles/tokens';

export const container = () => css`
  width: 200px;
  min-width: 200px;
  background-color: ${colorToken.bg[2]};
  border-radius: ${borderRadiusToken.input};
  margin-right: 20px;

  &:last-child {
    margin-right: 0;
  }
`;

export const image = () => css`
  position: relative;
  width: 100%;
  height: 60px;
  background: linear-gradient(90deg, #1dcfc1 0%, #ff9e42 100%);
  border-top-left-radius: ${borderRadiusToken.input};
  border-top-right-radius: ${borderRadiusToken.input};
`;

export const badge = () => css`
  position: absolute;
  top: 5px;
  right: 10px;
`;

export const content = () => css`
  width: 100%;
  height: 80px;
  padding: 10px;
  background-color: ${colorToken.bg[2]};
  border-bottom-left-radius: ${borderRadiusToken.input};
  border-bottom-right-radius: ${borderRadiusToken.input};
`;

export const text = () => css`
  text-align: left;
`;
