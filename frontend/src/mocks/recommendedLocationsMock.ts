import { recommendedLocation } from '@shared/types/recommendedLocation';

const recommendedLocationsMock: recommendedLocation[] = [
  {
    id: 1,
    index: 1,
    y: 37.4927431676548,
    x: 127.013867969161,
    name: '교대역',
    avgMinutes: 18,
    isBest: true,
    description:
      '조용하고 대화하기 좋은 카페들이 많아요! 📚 (교대역 부근의 스터디 카페들을 고려했습니다.) 그런데 이제 이렇게 길어지면 어떻게 될까요? 궁금하죠? 나도 궁금한데요.',
    reason:
      '조용하고 대화하기 좋은 카페들이 많아요! 📚 (교대역 부근의 스터디 카페들을 고려했습니다.)',
    places: [
      {
        index: 1,
        name: '컴포즈커피 서초교대점',
        category: '카페',
        walkingTime: 1,
        url: 'http://place.map.kakao.com/342258861',
      },
      {
        index: 2,
        name: '투썸플레이스 교대역점',
        category: '카페',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/21301236',
      },
      {
        index: 3,
        name: '이자카야나무 교대점',
        category: '술집',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/1755323566',
      },
    ],
    routes: [
      {
        startPlace: '성신여대입구',
        startingX: '127.016541',
        startingY: '37.592405',
        transferCount: '3',
        totalTravelTime: 30,
        paths: [
          {
            index: 0,
            startStation: '성신여대입구',
            endStation: '보문',
            lineCode: '113',
            travelTime: 1,
          },
          {
            index: 1,
            startStation: '보문',
            endStation: '약수',
            lineCode: '6',
            travelTime: 10,
          },
          {
            index: 2,
            startStation: '약수',
            endStation: '교대',
            lineCode: '3',
            travelTime: 16,
          },
        ],
      },
      {
        startPlace: '선릉',
        startingX: '127.049271',
        startingY: '37.504577',
        transferCount: '1',
        totalTravelTime: 8,
        paths: [
          {
            index: 0,
            startStation: '선릉',
            endStation: '교대',
            lineCode: '2',
            travelTime: 5,
          },
        ],
      },
      {
        startPlace: '잠실',
        startingX: '127.100164',
        startingY: '37.513346',
        transferCount: '1',
        totalTravelTime: 16,
        paths: [
          {
            index: 0,
            startStation: '잠실',
            endStation: '교대',
            lineCode: '2',
            travelTime: 13,
          },
        ],
      },
    ],
  },
  {
    id: 2,
    index: 2,
    y: 37.58204391787134,
    x: 127.00194500977393,
    name: '혜화역',
    avgMinutes: 20,
    isBest: false,
    description:
      '분위기 좋은 카페들이 많아 대화하기 좋아요! 🥰 (혜화역 부근의 다양한 분위기의 카페들을 고려했습니다.)',
    reason:
      '분위기 좋은 카페들이 많아 대화하기 좋아요! 🥰 (혜화역 부근의 다양한 분위기의 카페들을 고려했습니다.)',
    places: [
      {
        index: 1,
        name: '학림다방',
        category: '카페',
        walkingTime: 0,
        url: 'http://place.map.kakao.com/8143287',
      },
      {
        index: 2,
        name: '독일주택',
        category: '술집',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/25463398',
      },
      {
        index: 3,
        name: '서화커피',
        category: '카페',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/1223984349',
      },
    ],
    routes: [
      {
        startPlace: '성신여대입구',
        startingX: '127.016537',
        startingY: '37.592702',
        transferCount: '1',
        totalTravelTime: 6,
        paths: [
          {
            index: 0,
            startStation: '성신여대입구',
            endStation: '혜화',
            lineCode: '4',
            travelTime: 4,
          },
        ],
      },
      {
        startPlace: '선릉',
        startingX: '127.048606',
        startingY: '37.505274',
        transferCount: '3',
        totalTravelTime: 26,
        paths: [
          {
            index: 0,
            startStation: '선릉',
            endStation: '왕십리',
            lineCode: '116',
            travelTime: 11,
          },
          {
            index: 1,
            startStation: '왕십리',
            endStation: '동대문역사문화공원',
            lineCode: '2',
            travelTime: 8,
          },
          {
            index: 2,
            startStation: '동대문역사문화공원',
            endStation: '혜화',
            lineCode: '4',
            travelTime: 5,
          },
        ],
      },
      {
        startPlace: '잠실',
        startingX: '127.100164',
        startingY: '37.513346',
        transferCount: '2',
        totalTravelTime: 28,
        paths: [
          {
            index: 0,
            startStation: '잠실',
            endStation: '동대문역사문화공원',
            lineCode: '2',
            travelTime: 21,
          },
          {
            index: 1,
            startStation: '동대문역사문화공원',
            endStation: '혜화',
            lineCode: '4',
            travelTime: 5,
          },
        ],
      },
    ],
  },
  {
    id: 3,
    index: 3,
    y: 37.47656223234824,
    x: 126.98155858357366,
    name: '사당역',
    avgMinutes: 23,
    isBest: false,
    description:
      '다양한 카페와 맛집! 🗣️ 편안한 대화 가능! (사당역은 다양한 선택지를 제공합니다.)',
    reason:
      '다양한 카페와 맛집! 🗣️ 편안한 대화 가능! (사당역은 다양한 선택지를 제공합니다.)',
    places: [
      {
        index: 1,
        name: '스타벅스 사당점',
        category: '카페',
        walkingTime: 1,
        url: 'http://place.map.kakao.com/23447734',
      },
      {
        index: 2,
        name: '페니힐스',
        category: '카페',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/19392595',
      },
      {
        index: 3,
        name: '지금보고싶다 사당점',
        category: '술집',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/1903108718',
      },
    ],
    routes: [
      {
        startPlace: '성신여대입구',
        startingX: '127.016537',
        startingY: '37.592702',
        transferCount: '1',
        totalTravelTime: 33,
        paths: [
          {
            index: 0,
            startStation: '성신여대입구',
            endStation: '사당',
            lineCode: '4',
            travelTime: 31,
          },
        ],
      },
      {
        startPlace: '선릉',
        startingX: '127.049271',
        startingY: '37.504577',
        transferCount: '1',
        totalTravelTime: 14,
        paths: [
          {
            index: 0,
            startStation: '선릉',
            endStation: '사당',
            lineCode: '2',
            travelTime: 12,
          },
        ],
      },
      {
        startPlace: '잠실',
        startingX: '127.100164',
        startingY: '37.513346',
        transferCount: '1',
        totalTravelTime: 22,
        paths: [
          {
            index: 0,
            startStation: '잠실',
            endStation: '사당',
            lineCode: '2',
            travelTime: 20,
          },
        ],
      },
    ],
  },
  {
    id: 4,
    index: 4,
    y: 37.5568707448873,
    x: 126.923778562273,
    name: '홍대입구역',
    avgMinutes: 34,
    isBest: false,
    description:
      '개성있는 카페, 대화하기 좋은 공간 多! 🤩 (홍대입구역 부근의 다양한 컨셉의 카페들을 고려했습니다.)',
    reason:
      '개성있는 카페, 대화하기 좋은 공간 多! 🤩 (홍대입구역 부근의 다양한 컨셉의 카페들을 고려했습니다.)',
    places: [
      {
        index: 1,
        name: '카페공명 연남점',
        category: '카페',
        walkingTime: 5,
        url: 'http://place.map.kakao.com/1797970569',
      },
      {
        index: 2,
        name: '이미커피',
        category: '카페',
        walkingTime: 2,
        url: 'http://place.map.kakao.com/15985522',
      },
      {
        index: 3,
        name: '크래프트한스 연남직영점',
        category: '술집',
        walkingTime: 4,
        url: 'http://place.map.kakao.com/1224835048',
      },
    ],
    routes: [
      {
        startPlace: '성신여대입구',
        startingX: '127.016537',
        startingY: '37.592702',
        transferCount: '2',
        totalTravelTime: 29,
        paths: [
          {
            index: 0,
            startStation: '성신여대입구',
            endStation: '동대문역사문화공원',
            lineCode: '4',
            travelTime: 8,
          },
          {
            index: 1,
            startStation: '동대문역사문화공원',
            endStation: '홍대입구',
            lineCode: '2',
            travelTime: 19,
          },
        ],
      },
      {
        startPlace: '선릉',
        startingX: '127.048606',
        startingY: '37.505274',
        transferCount: '3',
        totalTravelTime: 34,
        paths: [
          {
            index: 0,
            startStation: '선릉',
            endStation: '선정릉',
            lineCode: '116',
            travelTime: 2,
          },
          {
            index: 1,
            startStation: '선정릉',
            endStation: '당산',
            lineCode: '9',
            travelTime: 21,
          },
          {
            index: 2,
            startStation: '당산',
            endStation: '홍대입구',
            lineCode: '2',
            travelTime: 9,
          },
        ],
      },
      {
        startPlace: '잠실',
        startingX: '127.100164',
        startingY: '37.513346',
        transferCount: '1',
        totalTravelTime: 41,
        paths: [
          {
            index: 0,
            startStation: '잠실',
            endStation: '홍대입구',
            lineCode: '2',
            travelTime: 39,
          },
        ],
      },
    ],
  },
];

export default recommendedLocationsMock;
