import { RecommendedLocation } from '@entities/types/Location';

import SpotItem from '@shared/components/spotItem/SpotItem';
import { flex } from '@shared/styles/default.styled';

import * as spotItemList from './spotItemList.styled';

interface SpotItemListProps {
  recommendedLocations: RecommendedLocation[];
}

function SpotItemList({ recommendedLocations }: SpotItemListProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), spotItemList.base()]}>
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
          />
        );
      })}
    </div>
  );
}

export default SpotItemList;
