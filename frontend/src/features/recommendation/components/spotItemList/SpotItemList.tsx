import SpotItem from '@shared/components/spotItem/SpotItem';
import { flex } from '@shared/styles/default.styled';

import { RecommendedLocation } from '@shared/types/LocationResponse';

interface SpotItemListProps {
  recommendedLocations: RecommendedLocation[];
  onSpotClick: (spot: RecommendedLocation) => void;
}

function SpotItemList({
  recommendedLocations,
  onSpotClick,
}: SpotItemListProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 })]}>
      {recommendedLocations.map((location) => {
        const { index, name, description, avgMinutes, isBest } = location;
        return (
          <SpotItem
            key={index}
            index={index}
            name={name}
            description={description}
            avgMinutes={avgMinutes}
            isBest={isBest}
            onClick={() => onSpotClick(location)}
          />
        );
      })}
    </div>
  );
}

export default SpotItemList;
