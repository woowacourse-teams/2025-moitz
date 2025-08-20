import { LocationRequirement } from '@entities/types/LocationRequirement';

export type RecommendationRequestBody = {
  startingPlaceNames: string[];
  requirement: LocationRequirement;
};

export type RecommendationResponse = {
  id: number;
};
