import { recommendedSpotItem } from '@shared/types/recommendedSpotItem';

const spotItemListMock: recommendedSpotItem[] = [
  {
    id: 1,
    index: 1,
    name: '서울역',
    description: '서울의 중심역, 교통의 허브',
    avgMinutes: 35,
    isBest: true,
  },
  {
    id: 2,
    index: 2,
    name: '강남역',
    description: '유동인구 많은 번화가',
    avgMinutes: 40,
    isBest: false,
  },
  {
    id: 3,
    index: 3,
    name: '잠실역',
    description: '롯데월드와 석촌호수 인근',
    avgMinutes: 25,
    isBest: false,
  },
  {
    id: 4,
    index: 4,
    name: '홍대입구역',
    description: '젊음의 거리와 예술의 거리',
    avgMinutes: 20,
    isBest: false,
  },
  {
    id: 5,
    index: 5,
    name: '신촌역',
    description: '대학교 인근, 맛집 거리',
    avgMinutes: 30,
    isBest: false,
  },
];

export default spotItemListMock;
