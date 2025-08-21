export interface SubwayLineInfo {
  name: string;
  code: number;
}

export const SUBWAY_LINE_INFO: { [key: string]: SubwayLineInfo } = {
  SEOUL_METRO_LINE1: { name: '1호선', code: 1 },
  SEOUL_METRO_LINE2: { name: '2호선', code: 2 },
  SEOUL_METRO_LINE3: { name: '3호선', code: 3 },
  SEOUL_METRO_LINE4: { name: '4호선', code: 4 },
  SEOUL_METRO_LINE5: { name: '5호선', code: 5 },
  SEOUL_METRO_LINE6: { name: '6호선', code: 6 },
  SEOUL_METRO_LINE7: { name: '7호선', code: 7 },
  SEOUL_METRO_LINE8: { name: '8호선', code: 8 },
  SEOUL_METRO_LINE9: { name: '9호선', code: 9 },
  GTX_A: { name: 'GTX_A', code: 91 },
  AIRPORT_RAILROAD: { name: '공항철도', code: 101 },
  GYEONGUI_JOUNGANG: { name: '경의중앙선', code: 104 },
  EVERLINE: { name: '용인 에버라인', code: 107 },
  GYEONGCHUN: { name: '경춘선', code: 108 },
  SIN_BUNDANG: { name: '신분당선', code: 109 },
  UIJEONGBU_LRT: { name: '의정부 경전철', code: 110 },
  GYEONGGANG: { name: '경강선', code: 112 },
  WOOE_SINSEUL_LRT: { name: '우이 신설선', code: 113 },
  SEOHAE_LINE: { name: '서해선', code: 114 },
  GIMPO_LRT: { name: '김포골드라인', code: 115 },
  SUIN_BUNDANG: { name: '수인분당선', code: 116 },
  SILLIM_LRT: { name: '신림선', code: 117 },
  INCHEON1: { name: '인천 1호선', code: 21 },
  INCHEON2: { name: '인천 2호선', code: 22 },
} as const;

export type LineCode =
  (typeof SUBWAY_LINE_INFO)[keyof typeof SUBWAY_LINE_INFO]['name'];
