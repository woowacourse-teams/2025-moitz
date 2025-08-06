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
  endStation: string;
  lineCode: string;
  travelTime: number;
};

export type RecommendedRoute = {
  startPlace: string;
  startingX: number;
  startingY: number;
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
};
export type Location = {
  places: RecommendedPlace[];
  routes: RecommendedRoute[];
} & RecommendedLocation;
