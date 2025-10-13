import { CONDITION_CARD_TEXT } from '@features/meeting/constants/conditionCard';

import { StartingPlace } from '@entities/location/types/Location';
import { LocationRequirement } from '@entities/location/types/LocationRequirement';

import StartingSpotName from '@shared/components/startingSpotName/StartingSpotName';
import { flex, typography } from '@shared/styles/default.styled';

import * as meetingWrapper from './meetingWrapper.styled';

interface StaringSpotWrapperProps {
  startingPlaces: StartingPlace[];
  conditionID: LocationRequirement;
}

const getCustomConditionIdText = (id: LocationRequirement) => {
  const entry = CONDITION_CARD_TEXT[id];
  return entry.ID === 'NOT_SELECTED' ? entry.TEXT : `${entry.TEXT} 장소`;
};

function MeetingWrapper({
  startingPlaces,
  conditionID,
}: StaringSpotWrapperProps) {
  const conditionIdText = getCustomConditionIdText(conditionID);

  return (
    <div css={[meetingWrapper.base(), flex({ direction: 'column', gap: 10 })]}>
      <div css={[flex({ align: 'center', gap: 10 })]}>
        <span css={[typography.sh1, meetingWrapper.title()]}>출발지</span>
        <div css={[flex({ wrap: 'wrap', gap: 5 })]}>
          {startingPlaces.map((place, index) => {
            const isLast = startingPlaces.length - 1 === index;
            return (
              <StartingSpotName
                key={place.index}
                place={place}
                isLast={isLast}
              />
            );
          })}
        </div>
      </div>
      <div css={[flex({ align: 'center', gap: 10 })]}>
        <span css={[typography.sh1, meetingWrapper.title()]}>조건</span>
        <span css={[typography.b2, meetingWrapper.content()]}>
          {conditionIdText}
        </span>
      </div>
    </div>
  );
}

export default MeetingWrapper;
