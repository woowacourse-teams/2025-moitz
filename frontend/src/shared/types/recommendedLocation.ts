export type recommendedLocation = {
  id: number;
  index: number;
  y: number;
  x: number;
  name: string;
  avgMinutes: number;
  isBest: boolean;
  description: string;
  reason: string;
  places: place[];
  routes: route[];
};

type place = {
  index: number;
  name: string;
  category: string;
  walkingTime: number;
  url: string;
};

type route = {
  startPlace: string;
  startingX: string;
  startingY: string;
  transferCount: string;
  totalTravelTime: number;
  paths: path[];
};

type path = {
  index: number;
  startStation: string;
  endStation: string;
  lineCode: string;
  travelTime: number;
};
