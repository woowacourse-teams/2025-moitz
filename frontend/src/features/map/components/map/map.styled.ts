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

export const top_overlay = () => css`
  position: absolute;
  width: 100%;
  top: 20px;
  padding: 0px 20px;
  z-index: 10;
`;
