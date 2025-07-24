import { spotItem } from '@shared/types/spotItem';

const spotItemListMock: spotItem[] = [
  {
    index: 1,
    name: '서울역',
    description: '서울의 중심역, 교통의 허브',
    avgMinutes: 35,
    isBest: true,
  },
  {
    index: 2,
    name: '강남역',
    description: '유동인구 많은 번화가',
    avgMinutes: 40,
    isBest: false,
  },
  {
    index: 3,
    name: '잠실역',
    description: '롯데월드와 석촌호수 인근',
    avgMinutes: 25,
    isBest: false,
  },
  {
    index: 4,
    name: '홍대입구역',
    description: '젊음의 거리와 예술의 거리',
    avgMinutes: 20,
    isBest: false,
  },
  {
    index: 5,
    name: '신촌역',
    description: '대학교 인근, 맛집 거리',
    avgMinutes: 30,
    isBest: false,
  },
  {
    index: 6,
    name: '사당역',
    description: '환승이 편리한 남부 교통 요지',
    avgMinutes: 50,
    isBest: false,
  },
  {
    index: 7,
    name: '왕십리역',
    description: '대형 쇼핑몰과 환승역',
    avgMinutes: 28,
    isBest: false,
  },
  {
    index: 8,
    name: '을지로입구역',
    description: '도심의 직장인 밀집 지역',
    avgMinutes: 18,
    isBest: false,
  },
  {
    index: 9,
    name: '고속터미널역',
    description: '터미널과 백화점이 함께',
    avgMinutes: 33,
    isBest: false,
  },
  {
    index: 10,
    name: '건대입구역',
    description: '맛집과 쇼핑의 거리',
    avgMinutes: 22,
    isBest: false,
  },
];

export default spotItemListMock;
