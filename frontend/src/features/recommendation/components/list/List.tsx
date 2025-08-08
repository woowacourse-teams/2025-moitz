import { RecommendedLocation, StartingPlace } from '@entities/types/Location';

import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';

import SpotItemList from '../spotItemList/SpotItemList';

interface ListProps {
  startingPlaces: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
  onSpotClick: (spot: RecommendedLocation) => void;
}

function List({
  startingPlaces,
  recommendedLocations,
  onSpotClick,
}: ListProps) {
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

export default List;
