import StartingSpotWrapper from '@shared/components/startingSpotWrapper/StartingSpotWrapper';

import { recommendedLocation } from '@shared/types/recommendedLocation';
import { startingLocation } from '@shared/types/startingLocation';

import SpotItemList from '../spotItemList/SpotItemList';

interface ListProps {
  startingLocations: startingLocation[];
  recommendedLocations: recommendedLocation[];
  onSpotClick: (spot: recommendedLocation) => void;
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
