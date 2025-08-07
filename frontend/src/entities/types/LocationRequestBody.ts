export type LocationRequirement =
  | 'CHAT'
  | 'MEETING'
  | 'FOCUS'
  | 'DATE'
  | 'NOT_SELECTED';

export type LocationRequestBody = {
  startingPlaceNames: string[];
  requirement: LocationRequirement;
};
