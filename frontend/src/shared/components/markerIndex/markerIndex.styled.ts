import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = () => css`
  width: 40px;
  min-width: 40px;
  height: 40px;
  border-radius: ${borderRadiusToken[100]};
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.main[1]};
`;

export const stroke = () => css`
  border: 4px solid ${colorToken.gray[8]};
`;

export const circle_base = () => css`
  border-radius: ${borderRadiusToken[100]};
`;

export const circle_font = () => css`
  transform: translate(0%, 5%);
`;

export const circle_recommended = () => css`
  width: 40px;
  min-width: 40px;
  height: 40px;
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.main[1]};
`;

export const circle_starting = () => css`
  width: 30px;
  min-width: 30px;
  height: 30px;
  color: ${colorToken.gray[8]};
  background-color: ${colorToken.orange[2]};
`;

export const label_base = () => css`
  font-weight: 600;
  font-size: 10px;
  text-shadow:
    -0.5px -0.5px 0 ${colorToken.gray[8]},
    0.5px -0.5px 0 ${colorToken.gray[8]},
    -0.5px 0.5px 0 ${colorToken.gray[8]},
    0.5px 0.5px 0 ${colorToken.gray[8]};
`;

export const label_recommended = () => css`
  color: ${colorToken.gray[2]};
`;

export const label_starting = () => css`
  color: ${colorToken.orange[1]};
`;
