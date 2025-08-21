export type StartingPlaceResponse = {
  id: number;
  index: number;
  x: number;
  y: number;
  name: string;
};

export type RecommendedPlaceResponse = {
  index: number;
  name: string;
  category: string;
  walkingTime: number;
  url: string;
};

export type RecommendedPathResponse = {
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

export type RecommendedRouteResponse = {
  startingPlaceId: number;
  transferCount: number;
  totalTravelTime: number;
  paths: RecommendedPathResponse[];
};

export type RecommendedLocationResponse = {
  id: number;
  index: number;
  x: number;
  y: number;
  name: string;
  avgMinutes: number;
  isBest: boolean;
  description: string;
  reason: string;
  places?: RecommendedPlaceResponse[];
  routes?: RecommendedRouteResponse[];
};

export type LocationResponse = {
  startingPlaces: StartingPlaceResponse[];
  locations: RecommendedLocationResponse[];
};
