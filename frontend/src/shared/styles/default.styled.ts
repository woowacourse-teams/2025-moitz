import { css } from '@emotion/react';

import { typoToken } from './tokens';

export const typography = {
  // Headers
  h1: css`
    font-size: ${typoToken.headers.h1};
    font-weight: ${typoToken.weight.bold};
  `,
  h2: css`
    font-size: ${typoToken.headers.h2};
    font-weight: ${typoToken.weight.bold};
  `,
  h3: css`
    font-size: ${typoToken.headers.h3};
    font-weight: ${typoToken.weight.bold};
  `,
  // Sub headers
  sh1: css`
    font-size: ${typoToken.subHeaders.sh1};
    font-weight: ${typoToken.weight.semiBold};
  `,
  sh2: css`
    font-size: ${typoToken.subHeaders.sh2};
    font-weight: ${typoToken.weight.semiBold};
  `,
  // Body
  b1: css`
    font-size: ${typoToken.body.b1};
    font-weight: ${typoToken.weight.regular};
  `,
  b2: css`
    font-size: ${typoToken.body.b2};
    font-weight: ${typoToken.weight.regular};
  `,
  // Captions
  c1: css`
    font-size: ${typoToken.captions.c1};
    font-weight: ${typoToken.weight.regular};
  `,
};
