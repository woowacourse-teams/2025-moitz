import { typography } from '../../styles/default.styled';

import * as marker from './markerIndex.styled';

interface MarkerIndexProps {
  index: number;
  hasStroke?: boolean;
}

function MarkerIndex({ index, hasStroke = false }: MarkerIndexProps) {
  return (
    <div css={[marker.base(), hasStroke && marker.stroke()]}>
      <span css={typography.h3}>{index}</span>
    </div>
  );
}

export default MarkerIndex;
