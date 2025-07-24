import SpotItem from '@shared/components/spotItem/SpotItem';
import { flex } from '@shared/styles/default.styled';

import { spotItem } from '@shared/types/spotItem';

import * as spotItemList from './spotItemList.styled';

interface SpotItemListProps {
  itemList: spotItem[];
}

function SpotItemList({ itemList }: SpotItemListProps) {
  return (
    <div css={[flex({ direction: 'column', gap: 20 }), spotItemList.base()]}>
      {itemList.map((item) => {
        const { index, name, description, avgMinutes, isBest } = item;
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
