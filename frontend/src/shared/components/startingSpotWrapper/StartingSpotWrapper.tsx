import { StartingPlace } from '@entities/types/Location';

import StartingSpotName from '@shared/components/startingSpotName/StartingSpotName';
import { flex, typography } from '@shared/styles/default.styled';

import * as startSpotWrapper from './startingSpotWrapper.styled';

interface StaringSpotWrapperProps {
  startingLocations: StartingPlace[];
}

function StartingSpotWrapper({ startingLocations }: StaringSpotWrapperProps) {
  return (
    <div css={[flex({ align: 'center', gap: 10 }), startSpotWrapper.base()]}>
      <span css={[typography.sh1, startSpotWrapper.title()]}>출발지</span>
      <div css={[flex({ wrap: 'wrap', gap: 5 })]}>
        {startingLocations.map((location, index) => {
          const isLast = startingLocations.length - 1 === index;
          return (
            <StartingSpotName
              key={location.index}
              location={location}
              isLast={isLast}
            />
          );
        })}
      </div>
    </div>
  );
}

export default StartingSpotWrapper;
