export type StartingPlace = {
  id: number;
  index: number;
  x: number;
  y: number;
  name: string;
};

export type RecommendedPlace = {
  index: number;
  name: string;
  category: string;
  walkingTime: number;
  url: string;
};

export type RecommendedPath = {
  index: number;
  startStation: string;
  startingX: number;
  startingY: number;
  endStation: string;
  endingX: number;
  endingY: number;
  lineCode: string;
  travelTime: number;
};

export type RecommendedRoute = {
  startingPlaceId: number;
  transferCount: number;
  totalTravelTime: number;
  paths: RecommendedPath[];
};

export type RecommendedLocation = {
  id: number;
  index: number;
  x: number;
  y: number;
  name: string;
  avgMinutes: number;
  isBest: boolean;
  description: string;
  reason: string;
  places?: RecommendedPlace[];
  routes?: RecommendedRoute[];
};

export type Location = {
  startingPlaces: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
};
