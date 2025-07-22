/** @jsxImportSource @emotion/react */
import { inline_flex, typography } from '../../styles/default.styled';

import * as mapPoint from './mapPoint.styled';

interface MapPointProps {
  text: string;
}

function MapPoint({ text }: MapPointProps) {
  return (
    <div
      css={[
        inline_flex({ justify: 'center', align: 'center', gap: 10 }),
        mapPoint.base(),
      ]}
    >
      <div css={mapPoint.dot()}></div>
      <span css={typography.sh2}>{text}</span>
    </div>
  );
}

export default MapPoint;
