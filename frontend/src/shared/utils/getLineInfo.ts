import { SUBWAY_LINE_INFO } from '@entities/types/LineCode';

export const getLineInfo = (lineName: string) => {
  const lineInfo = Object.values(SUBWAY_LINE_INFO).find(
    (info) => info.name === lineName,
  );
  return lineInfo;
};
