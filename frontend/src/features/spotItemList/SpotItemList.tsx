import SpotItem from '@shared/components/spotItem/SpotItem';
import { flex } from '@shared/styles/default.styled';

function SpotItemList() {
  return (
    <div css={flex({ direction: 'column', gap: 20 })}>
      <SpotItem
        index={1}
        name={'가오리'}
        description={'가오리로 모여라'}
        avgMinutes={30}
        isBest={true}
      />
      <SpotItem
        index={1}
        name={'가오리'}
        description={'가오리로 모여라'}
        avgMinutes={30}
        isBest={false}
      />
    </div>
  );
}

export default SpotItemList;
