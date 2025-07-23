import SpotItemList from '@features/spotItemList/SpotItemList';

import spotItemListMock from '../../mocks/spotItemListMock';

function ResultPage() {
  return <SpotItemList itemList={spotItemListMock} />;
}

export default ResultPage;
