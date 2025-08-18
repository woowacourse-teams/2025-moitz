import { flex, typography, shadow } from '@shared/styles/default.styled';

import * as marker from './markerIndex.styled';

interface MarkerIndexProps {
  index: number | string;
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
          gap: 6,
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
        <span css={[typography.h3, marker.circle_font()]}>{index}</span>
      </div>
      {label && <p css={[marker.label_base(), label_type()]}>{label}</p>}
    </div>
  );
}

export default MarkerIndex;
