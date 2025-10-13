import { css } from '@emotion/react';

import { colorToken, borderRadiusToken } from '@shared/styles/tokens';

const MIN_VH = 12; // 최소 높이
const MAX_VH = 90; // 최대 높이

export const base = () => css`
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
`;

export const container = (positionPercent: number) => css`
  width: 100%;
  @media (min-width: 400px) {
    width: 400px;
    margin: auto;
  }

  height: ${MIN_VH + (MAX_VH - MIN_VH) * (positionPercent / 100)}dvh;
  min-height: ${MIN_VH}dvh;
  max-height: ${MAX_VH}dvh;

  padding: 0px 20px;
  background-color: ${colorToken.gray[8]};
  border-top-left-radius: ${borderRadiusToken[10]};
  border-top-right-radius: ${borderRadiusToken[10]};
`;

export const header = () => css`
  padding: 5px 0px;
  cursor: grab;

  /* 브라우저의 기본 터치 제스처(스크롤, 스와이프, 더블탭 확대, 핀치줌 등)를 전부 끄겠다는 CSS 설정 */
  /* 핸들을 끌 때 페이지가 스크롤돼서 드래그가 끊기는 걸 방지 */
  touch-action: none;
  user-select: none;
  -webkit-user-select: none;

  &:active {
    cursor: grabbing;
  }
`;

export const handle = () => css`
  width: 40px;
  height: 4px;
  border-radius: 2px;
  margin: 8px auto;
  display: block;
  background-color: ${colorToken.gray[7]};

  /* 현재 handle을 장식용 막대로 구현한 상태임 */
  /* 요소 위를 눌러도 클릭/드래그 타깃이 되지 않고, 이벤트가 뒤(혹은 부모)로 통과 */
  pointer-events: none;
`;

export const content = () => css`
  padding-bottom: 20px;
  min-height: 0;
  overflow: auto; //  /콘텐츠 많을 때 내부 스크롤
  overscroll-behavior: contain; // 바디로 스크롤 전파 방지
`;

export const animate = () => css`
  transition: height 220ms cubic-bezier(0.2, 0.8, 0.2, 1);

  // 사용자가 '애니메이션 줄이기'를 켜둔 경우에 맞춰 애니메이션/트랜지션을 꺼 주는 접근성 설정
  @media (prefers-reduced-motion: reduce) {
    transition: none;
  }
`;
