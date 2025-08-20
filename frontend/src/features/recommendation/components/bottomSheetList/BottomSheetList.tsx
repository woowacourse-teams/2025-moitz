import {
  RecommendedLocation,
  StartingPlace,
} from '@entities/location/types/Location';

import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';

import SpotItemList from '../spotItemList/SpotItemList';

interface BottomSheetListProps {
  startingPlaces: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  onSpotClick: (spot: RecommendedLocation) => void;
}

function BottomSheetList({
  startingPlaces,
  recommendedLocations,
  onSpotClick,
}: BottomSheetListProps) {
  return (
    <>
      <StartingSpotWrapper startingPlaces={startingPlaces} />
      <SpotItemList
        recommendedLocations={recommendedLocations}
        onSpotClick={onSpotClick}
      />
    </>
  );
}

export default BottomSheetList;
