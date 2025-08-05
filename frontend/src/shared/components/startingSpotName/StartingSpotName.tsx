import { flex, typography } from '@shared/styles/default.styled';

import { startingLocation } from '@shared/types/startingLocation';

import * as startingSpotNameStyled from './startingSpotName.styled';

interface StartSpotNameProps {
  location: startingLocation;
  isLast: boolean;
}

function StartingSpotName({ location, isLast }: StartSpotNameProps) {
  return (
    <div
      key={location.index}
      css={flex({
        justify: 'center',
        align: 'center',
        gap: 5,
      })}
    >
      <span css={[typography.b2, startingSpotNameStyled.nameList()]}>
        {location.name}
      </span>
      <div css={!isLast && startingSpotNameStyled.dot()}></div>
    </div>
  );
}

export default StartingSpotName;
