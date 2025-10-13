import { css } from '@emotion/react';

import { getLineInfo } from '@shared/lib/getLineInfo';
import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const container = () => css`
  position: relative;
  width: 100%;
  height: 15px;
  background-color: ${colorToken.gray[7]};
  border-radius: ${borderRadiusToken[10]};
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
  min-width: fit-content;
  flex-shrink: 1;
  height: 15px;
  padding: 0 5px;
  background-color: ${lineCode
    ? colorToken.subway[getLineInfo(lineCode).code]
    : colorToken.gray[7]};
  border-radius: ${borderRadiusToken[10]};
`;

export const text = (lineCode: string) => css`
  margin: 0 3px;
  color: ${lineCode ? colorToken.gray[8] : colorToken.gray[5]};
  white-space: nowrap;
`;
