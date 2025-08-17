import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

export const base = (positionPercent: number) => css`
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  transform: translateY(${100 - positionPercent}%);
`;

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

    /* 브라우저의 기본 터치 제스처(스크롤, 스와이프, 더블탭 확대, 핀치줌 등)를 전부 끄겠다는 CSS 설정 */
    /* 핸들을 끌 때 페이지가 스크롤돼서 드래그가 끊기는 걸 방지 */
    touch-action: none;
    user-select: none;
    -webkit-user-select: none;
  }
`;

export const handle = () => css`
  width: 40px;
  height: 4px;
  background-color: ${colorToken.gray[7]};
  border-radius: 2px;
  margin: 8px auto;
  display: block;

  /* 현재 handle을 장식용 막대로 구현한 상태임 */
  /* 요소 위를 눌러도 클릭/드래그 타깃이 되지 않고, 이벤트가 뒤(혹은 부모)로 통과 */
  pointer-events: none;
`;

export const content = () => css`
  padding-bottom: 20px;
`;
