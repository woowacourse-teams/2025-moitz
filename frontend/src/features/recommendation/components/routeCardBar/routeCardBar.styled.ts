import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';
import { getLineInfo } from '@shared/utils/getLineInfo';

export const container = () => css`
  position: relative;
  width: 100%;
  height: 15px;
  background-color: ${colorToken.gray[7]};
  border-radius: ${borderRadiusToken.input};
`;

export const base = () => css`
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
`;

export const path = (
  lineCode: string,
  travelTime: number,
  totalTravelTime: number,
) => css`
  width: ${(travelTime / totalTravelTime) * 100}%;
  height: 15px;
  padding: 0 5px;
  background-color: ${lineCode
    ? colorToken.subway[getLineInfo(lineCode).code]
    : colorToken.gray[7]};
  border-radius: ${borderRadiusToken.input};
`;

export const text = (lineCode: string) => css`
  color: ${lineCode ? colorToken.gray[8] : colorToken.gray[5]};
`;
