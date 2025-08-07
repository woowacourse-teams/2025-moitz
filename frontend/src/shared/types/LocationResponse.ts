export type LocationResponse = {
  startingPlaces: StartingPlace[];
  recommendedLocations: RecommendedLocation[];
};

export type StartingPlace = {
  id: number;
  index: number;
  x: number;
  y: number;
  name: string;
};

export type RecommendedLocation = {
  id: number;
  index: number;
  y: number;
  x: number;
  name: string;
  avgMinutes: number;
  isBest: boolean;
  description: string;
  reason: string;
  places: Place[];
  routes: Route[];
};

export type Place = {
  index: number;
  name: string;
  category: string;
  walkingTime: number;
  url: string;
};

export type Route = {
  startingPlaceId: number;
  transferCount: number;
  totalTravelTime: number;
  paths: Path[];
};

type Path = {
  index: number;
  startStation: string;
  startingX: number;
  startingY: number;
  endStation: string;
  endingX: number;
  endingY: number;
  lineCode: string | null;
  travelTime: number;
};
