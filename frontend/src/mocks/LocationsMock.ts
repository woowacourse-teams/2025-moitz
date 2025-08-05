import { Location } from '@entities/types/Location';

const LocationsMock: Location[] = [
  {
    id: 1,
    index: 1,
    y: 37.54040751726388,
    x: 127.06920291650829,
    name: '건대입구역',
    avgMinutes: 19,
    isBest: true,
    description: '핫플레이스! ✨ 노래방 당연히 있고, 맛집도 다양!',
    reason: '핫플레이스! ✨ 노래방 당연히 있고, 맛집도 다양!',
    places: [
      {
        index: 1,
        name: '락휴 코인노래연습장 건대입구역점',
        category: '코인노래방',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/75632985',
      },
      {
        index: 2,
        name: '악쓰는하마시즌2',
        category: '노래방',
        walkingTime: 1,
        url: 'http://place.map.kakao.com/834141694',
      },
      {
        index: 3,
        name: '락휴코인노래연습장 서울건대점',
        category: '코인노래방',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/775778672',
      },
    ],
    routes: [
      {
        startPlace: '강변',
        startingX: 127.094687,
        startingY: 37.535092,
        transferCount: 1,
        totalTravelTime: 6,
        paths: [
          {
            index: 0,
            startStation: '강변',
            endStation: '건대입구',
            lineCode: '2',
            travelTime: 4,
          },
        ],
      },
      {
        startPlace: '동대문',
        startingX: 127.009111,
        startingY: 37.571132,
        transferCount: 2,
        totalTravelTime: 18,
        paths: [
          {
            index: 0,
            startStation: '동대문',
            endStation: '동대문역사문화공원',
            lineCode: '4',
            travelTime: 2,
          },
          {
            index: 1,
            startStation: '동대문역사문화공원',
            endStation: '건대입구',
            lineCode: '2',
            travelTime: 13,
          },
        ],
      },
      {
        startPlace: '서울대입구',
        startingX: 126.952725,
        startingY: 37.481199,
        transferCount: 1,
        totalTravelTime: 35,
        paths: [
          {
            index: 0,
            startStation: '서울대입구',
            endStation: '건대입구',
            lineCode: '2',
            travelTime: 33,
          },
        ],
      },
    ],
  },
];

export default LocationsMock;
