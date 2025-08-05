import SpotItem from '@shared/components/spotItem/SpotItem';
import { flex } from '@shared/styles/default.styled';

import { recommendedLocation } from '@shared/types/recommendedLocation';

interface SpotItemListProps {
  recommendedLocations: recommendedLocation[];
  onSpotClick: (spot: recommendedLocation) => void;
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
