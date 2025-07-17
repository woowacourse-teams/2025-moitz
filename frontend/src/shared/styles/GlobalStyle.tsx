import { Global, css } from '@emotion/react';

function GlobalStyle() {
  return (
    <>
      <link href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/static/pretendard.css" rel="stylesheet" />
      <Global
        styles={css`
          * {
            font-family: 'Pretendard';
          }
        `}
      />
    </>
  );
}

export default GlobalStyle;
