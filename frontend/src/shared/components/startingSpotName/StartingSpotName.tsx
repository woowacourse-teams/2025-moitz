import { StartingPlace } from '@entities/types/Location';

import { flex, typography } from '@shared/styles/default.styled';

import * as startingSpotNameStyled from './startingSpotName.styled';

interface StartSpotNameProps {
  place: StartingPlace;
  isLast: boolean;
}

function StartingSpotName({ place, isLast }: StartSpotNameProps) {
  return (
    <div
      key={place.index}
      css={flex({
        justify: 'center',
        align: 'center',
        gap: 5,
      })}
    >
      <span css={[typography.b2, startingSpotNameStyled.nameList()]}>
        {place.name}
      </span>
      <div css={!isLast && startingSpotNameStyled.dot()}></div>
    </div>
  );
}

export default StartingSpotName;
