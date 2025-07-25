import { css } from '@emotion/react';

export const container = () => css`
  position: relative;
  width: 100%;
  height: 100%;
`;

export const base = () => css`
  position: absolute;
  width: 100%;
  height: 100%;
`;

export const overlay = () => css`
  position: absolute;
  width: 100%;
  top: 16px;
  padding: 0px 16px;
  z-index: 10;
`;
