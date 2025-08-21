import { RecommendationRequestBody } from '@entities/location/api/types/RecommendationIdAPI';

export const LocationsRequestBodyMock: RecommendationRequestBody = {
  startingPlaceNames: ['강변역', '동대문역', '서울대입구역'],
  requirement: 'CHAT',
};
