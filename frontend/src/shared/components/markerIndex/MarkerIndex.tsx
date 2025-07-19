/** @jsxImportSource @emotion/react */
import * as marker from './markerIndex.styled';

interface MarkerIndexProps {
  index: number;
}

function MarkerIndex({ index }: MarkerIndexProps) {
  return (
    <div css={marker.base()}>
      <span>{index}</span>
    </div>
  );
}

export default MarkerIndex;
