import { flex, typography, shadow } from '@shared/styles/default.styled';

import * as marker from './markerIndex.styled';
import { label_base } from './markerIndex.styled';

interface MarkerIndexProps {
  index: number;
  type: 'recommended' | 'starting';
  label?: string;
  hasStroke?: boolean;
  hasShadow?: boolean;
}

function MarkerIndex({
  index,
  type,
  label = '',
  hasStroke = false,
  hasShadow = false,
}: MarkerIndexProps) {
  const circle_type =
    type === 'recommended' ? marker.circle_recommended : marker.circle_starting;
  const label_type =
    type === 'recommended' ? marker.label_recommended : marker.label_starting;

  return (
    <div
      css={[
        flex({
          direction: 'column',
          justify: 'center',
          align: 'center',
          gap: 5,
        }),
      ]}
    >
      <div
        css={[
          flex({ justify: 'center', align: 'center' }),
          marker.circle_base(),
          circle_type(),
          hasStroke && marker.stroke(),
          hasShadow && shadow.map,
        ]}
      >
        <span css={typography.h3}>{index}</span>
      </div>
      {label && <p css={[label_base(), label_type()]}>{label}</p>}
    </div>
  );
}

export default MarkerIndex;
