/** @jsxImportSource @emotion/react */

import Badge from '@shared/components/badge/Badge';
import MarkerIndex from '@shared/components/markerIndex/MarkerIndex';
import { flex, typography } from '@shared/styles/default.styled';

import * as spotItem from './spotItem.styled';

interface SpotItemProps {
  index: number;
  name: string;
  description: string;
  avgMinutes: number;
  isBest: boolean;
  onClick: () => void;
}

function SpotItem({
  index,
  name,
  description,
  avgMinutes,
  isBest = false,
  onClick,
}: SpotItemProps) {
  return (
    <div
      css={[
        flex({ justify: 'center', align: 'center', gap: 15 }),
        spotItem.base(),
      ]}
      onClick={onClick}
    >
      <MarkerIndex index={index} />
      <div
        css={[
          flex({ direction: 'column', gap: 10 }),
          spotItem.contents_container(),
        ]}
      >
        <div css={flex({ justify: 'space-between', align: 'center' })}>
          <div css={flex({ align: 'center', gap: 14 })}>
            <span css={typography.h3}>{name}</span>
            {isBest && <Badge text="best" />}
          </div>
          <span css={typography.c1}>평균 {avgMinutes}분</span>
        </div>
        <p css={[typography.c1, spotItem.description()]}>{description}</p>
      </div>
    </div>
  );
}

export default SpotItem;
