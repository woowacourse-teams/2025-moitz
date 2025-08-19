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
  subway: {
    1: 'var(--color-subway-line-1)',
    2: 'var(--color-subway-line-2)',
    3: 'var(--color-subway-line-3)',
    4: 'var(--color-subway-line-4)',
    5: 'var(--color-subway-line-5)',
    6: 'var(--color-subway-line-6)',
    7: 'var(--color-subway-line-7)',
    8: 'var(--color-subway-line-8)',
    9: 'var(--color-subway-line-9)',
    91: 'var(--color-subway-line-91)',
    101: 'var(--color-subway-line-101)',
    104: 'var(--color-subway-line-104)',
    107: 'var(--color-subway-line-107)',
    108: 'var(--color-subway-line-108)',
    109: 'var(--color-subway-line-109)',
    110: 'var(--color-subway-line-110)',
    112: 'var(--color-subway-line-112)',
    113: 'var(--color-subway-line-113)',
    114: 'var(--color-subway-line-114)',
    115: 'var(--color-subway-line-115)',
    116: 'var(--color-subway-line-116)',
    117: 'var(--color-subway-line-117)',
    21: 'var(--color-subway-line-21)',
    22: 'var(--color-subway-line-22)',
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
    sh3: 'var(--font-subheader-sh3)',
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
  10: 'var(--radius-10)',
  14: 'var(--radius-14)',
  20: 'var(--radius-20)',
  100: 'var(--radius-100)',
};

export { colorToken, typoToken, borderRadiusToken };
