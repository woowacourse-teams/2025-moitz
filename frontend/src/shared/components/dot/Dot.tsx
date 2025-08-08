import { colorToken } from '@shared/styles/tokens';

import * as dot from './dot.styled';

interface DotProps {
  size: number;
  colorType: keyof typeof colorToken;
  colorTokenIndex: number;
}

function Dot({ size, colorType, colorTokenIndex }: DotProps) {
  return <div css={dot.base(size, colorType, colorTokenIndex)}></div>;
}

export default Dot;
