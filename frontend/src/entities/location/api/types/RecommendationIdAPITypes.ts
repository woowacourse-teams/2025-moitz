import { LocationRequirement } from '@entities/location/types/LocationRequirement';

export type RecommendationRequestBody = {
  startingPlaceNames: string[];
  requirement: LocationRequirement;
};

export type RecommendationResponse = {
  id: string;
};
