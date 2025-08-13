import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const container = () => css`
  width: 100%;
  height: 50vh;
  min-height: 50vh;
  padding: 0px 20px 0px 20px;
  background-color: ${colorToken.gray[8]};
  border-top-left-radius: ${borderRadiusToken.input};
  border-top-right-radius: ${borderRadiusToken.input};
`;

export const header = () => css`
  padding: 10px 0px 20px 0px;
  cursor: grab;

  &:active {
    cursor: grabbing;
  }
`;

export const handle = () => css`
  width: 40px;
  height: 4px;
  background-color: ${colorToken.gray[7]};
  border-radius: 2px;
  margin: 8px auto;
  display: block;
`;

export const content = () => css`
  padding-bottom: 20px;
`;
