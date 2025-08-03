export interface RecommendedPlace {
  index: number;
  name: string;
  category: string;
  walkingTime: number;
  url: string;
}

export interface RecommendedPath {
  index: number;
  startStation: string;
  endStation: string;
  lineCode: string;
  travelTime: number;
}

export interface RecommendedRoute {
  startPlace: string;
  startingX: number;
  startingY: number;
  transferCount: number;
  totalTravelTime: number;
  paths: RecommendedPath[];
}

export interface RecommendedLocation {
  id: number;
  index: number;
  x: number;
  y: number;
  name: string;
  avgMinutes: number;
  isBest: boolean;
  description: string;
  reason: string;
}
export interface Location extends RecommendedLocation {
  places: RecommendedPlace[];
  routes: RecommendedRoute[];
}
