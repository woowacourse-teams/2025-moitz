import { css } from '@emotion/react';

export default function App() {
  return (
    <div
      css={css`
        padding: 32px;
        background-color: hotpink;
        font-size: 24px;
        border-radius: 4px;
        &:hover {
          color: red;
        }
      `}
    ></div>
  );
}
