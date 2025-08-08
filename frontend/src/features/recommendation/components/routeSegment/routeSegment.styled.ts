import { css } from '@emotion/react';

export const base = () => css`
  width: 100%;
  height: 100%;
`;

export const station_name = () => css`
  width: 70px;
`;

export const title = (color: string) => css`
  color: ${color};
`;
