import './tokens.css';

const colorToken = {
  gray: {
    1: 'var(--color-gray-1)',
    2: 'var(--color-gray-2)',
    3: 'var(--color-gray-3)',
    4: 'var(--color-gray-4)',
    5: 'var(--color-gray-5)',
    6: 'var(--color-gray-6)',
    7: 'var(--color-gray-7)',
    8: 'var(--color-gray-8)',
  },
  main: {
    1: 'var(--color-main-1)',
    2: 'var(--color-main-2)',
    3: 'var(--color-main-3)',
    4: 'var(--color-main-4)',
  },
  sub: {
    1: 'var(--color-sub-1)',
    2: 'var(--color-sub-2)',
  },
  orange: {
    1: 'var(--color-orange-1)',
    2: 'var(--color-orange-2)',
  },
  bg: {
    1: 'var(--color-bg-1)',
    2: 'var(--color-bg-2)',
  },
};

const typoToken = {
  headers: {
    h1: 'var(--font-header-h1)',
    h2: 'var(--font-header-h2)',
    h3: 'var(--font-header-h3)',
  },
  subHeaders: {
    sh1: 'var(--font-subheader-sh1)',
    sh2: 'var(--font-subheader-sh2)',
  },
  body: {
    b1: 'var(--font-body-b1)',
    b2: 'var(--font-body-b2)',
  },
  captions: {
    c1: 'var(--font-caption-c1)',
    c2: 'var(--font-caption-c2)',
  },
  weight: {
    bold: 'var(--font-weight-bold)',
    semiBold: 'var(--font-weight-semi-bold)',
    regular: 'var(--font-weight-regular)',
  },
};

const borderRadiusToken = {
  input: 'var(--radius-input)',
  button: 'var(--radius-button)',
  global: 'var(--radius-global)',
  round: 'var(--radius-round)',
};

export { colorToken, typoToken, borderRadiusToken };
