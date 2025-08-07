import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';

import { RecommendedLocation } from '@shared/types/LocationResponse';
import { startingLocation } from '@shared/types/startingLocation';

import SpotItemList from '../spotItemList/SpotItemList';

interface ListProps {
  startingLocations: startingLocation[];
  recommendedLocations: RecommendedLocation[];
  onSpotClick: (spot: RecommendedLocation) => void;
}

function List({
  startingLocations,
  recommendedLocations,
  onSpotClick,
}: ListProps) {
  return (
    <>
      <StartingSpotWrapper startingLocations={startingLocations} />
      <SpotItemList
        recommendedLocations={recommendedLocations}
        onSpotClick={onSpotClick}
      />
    </>
  );
}

export default List;
