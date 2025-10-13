const createLoadingText = (text: string) => [
  `${text}`,
  `${text}.`,
  `${text}..`,
  `${text}...`,
];

const LOADING_MESSAGES = [
  '모두가 편하게 만날 수 있는 장소를 찾고 있어요',
  '출발지의 경위도를 찾고 있어요',
  '각 출발지별 소요 시간을 계산하고 있어요',
  'AI가 대중교통 데이터를 학습하고 있어요',
  'AI가 각자의 이동 거리를 최적화하고 있어요',
  '인공지능이 주변의 맛집과 카페를 분석하고 있어요',
  '빅데이터를 기반으로 최적의 장소를 찾고 있어요',
  '어디에서 만날지 정하고 있어요',
  '지도를 그리고 있어요',
  '모잇지가 여러분의 선호도를 반영하고 있어요',
];

export const LOADING_TEXT = LOADING_MESSAGES.flatMap(createLoadingText);
