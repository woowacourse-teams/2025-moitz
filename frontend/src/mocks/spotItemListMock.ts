import { spotItem } from '@shared/types/spotItem';

const spotItemListMock: spotItem[] = [
  {
    index: 1,
    name: '가오리',
    description: '가오리로 모여라',
    avgMinutes: 30,
    isBest: true,
  },
  {
    index: 2,
    name: '돌고래',
    description: '돌고래와 함께 수영',
    avgMinutes: 45,
    isBest: false,
  },
  {
    index: 3,
    name: '해마',
    description: '해마를 찾아서',
    avgMinutes: 20,
    isBest: true,
  },
  {
    index: 4,
    name: '상어',
    description: '상어 관찰 포인트',
    avgMinutes: 60,
    isBest: false,
  },
];

export default spotItemListMock;
