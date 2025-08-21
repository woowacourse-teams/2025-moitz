import { inline_flex, typography, shadow } from '@shared/styles/default.styled';

import Dot from '../dot/Dot';

import * as mapPoint from './mapPoint.styled';

interface MapPointProps {
  text: string;
}

function MapPoint({ text }: MapPointProps) {
  return (
    <div
      css={[
        inline_flex({ justify: 'center', align: 'center', gap: 10 }),
        shadow.map,
        mapPoint.base(),
        mapPoint.floating(),
      ]}
    >
      <Dot size={8} colorType="main" colorTokenIndex={1} />
      <span css={typography.sh2}>{text}</span>
    </div>
  );
}

export default MapPoint;
