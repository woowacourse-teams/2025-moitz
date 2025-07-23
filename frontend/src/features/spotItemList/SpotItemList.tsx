import SpotItem from '@shared/components/spotItem/SpotItem';
import { flex } from '@shared/styles/default.styled';

import spotItemsMock from './spotItemListMock';

function SpotItemList() {
  return (
    <div css={flex({ direction: 'column', gap: 20 })}>
      {spotItemsMock.map((item) => (
        <SpotItem
          key={item.index}
          index={item.index}
          name={item.name}
          description={item.description}
          avgMinutes={item.avgMinutes}
          isBest={item.isBest}
        />
      ))}
    </div>
  );
}

export default SpotItemList;
