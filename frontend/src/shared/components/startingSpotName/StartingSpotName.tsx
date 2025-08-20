import { StartingPlace } from '@entities/location/types/Location';

import { flex, typography } from '@shared/styles/default.styled';

import Dot from '../dot/Dot';

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
      {!isLast && <Dot size={3} colorType="main" colorTokenIndex={1} />}
    </div>
  );
}

export default StartingSpotName;
