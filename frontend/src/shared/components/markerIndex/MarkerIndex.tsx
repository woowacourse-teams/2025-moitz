import * as button from './markerIndex.styled';

interface MarkerIndexProps {
  index: number;
}

function MarkerIndex({ index }: MarkerIndexProps) {
  return (
    <div css={button.base()}>
      <div>{index}</div>
    </div>
  );
}

export default MarkerIndex;
