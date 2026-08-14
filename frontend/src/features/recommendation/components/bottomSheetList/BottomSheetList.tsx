import React from 'react';

import {
  RecommendedLocation,
  StartingPlace,
} from '@entities/location/types/Location';
import { LocationRequirement } from '@entities/location/types/LocationRequirement';

import MeetingWrapper from '@shared/components/meetingWrapper/MeetingWrapper';

import SpotItemList from '../spotItemList/SpotItemList';

interface BottomSheetListProps {
  startingPlaces: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  conditionIDs: LocationRequirement[];
  onSpotClick: (spot: RecommendedLocation) => void;
}

function BottomSheetList({
  startingPlaces,
  recommendedLocations,
  conditionIDs,
  onSpotClick,
}: BottomSheetListProps) {
  return (
    <>
      <MeetingWrapper
        startingPlaces={startingPlaces}
        conditionIDs={conditionIDs}
      />
      <SpotItemList
        recommendedLocations={recommendedLocations}
        onSpotClick={onSpotClick}
      />
    </>
  );
}

export default React.memo(BottomSheetList);
