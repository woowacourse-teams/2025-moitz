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

// Flex
type flexOptions = {
  direction?: 'row' | 'column';
  justify?:
    | 'flex-start'
    | 'center'
    | 'flex-end'
    | 'space-between'
    | 'space-around';
  align?: 'flex-start' | 'center' | 'flex-end' | 'stretch';
  gap?: number;
};

export const flex = (options: flexOptions = {}) => css`
  display: flex;
  ${options.direction && `flex-direction: ${options.direction};`}
  ${options.justify && `justify-content: ${options.justify};`}
  ${options.align && `align-items: ${options.align};`}
  ${options.gap && `gap: ${options.gap}px;`}
`;

export const inline_flex = (options: flexOptions = {}) => css`
  display: inline-flex;
  ${options.direction && `flex-direction: ${options.direction};`}
  ${options.justify && `justify-content: ${options.justify};`}
  ${options.align && `align-items: ${options.align};`}
  ${options.gap && `gap: ${options.gap}px;`}
`;

export const grid_padding = css`
  padding: 0 20px;
`;
