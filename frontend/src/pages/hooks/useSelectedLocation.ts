import { useState } from 'react';

import { RecommendedLocation } from '@entities/types/Location';

type useSelectedLocationReturn = {
  selectedLocation: RecommendedLocation | null;
  changeSelectedLocation: (location: RecommendedLocation) => void;
};

const useSelectedLocation = (): useSelectedLocationReturn => {
  const [selectedLocation, setSelectedLocation] =
    useState<RecommendedLocation | null>(null);

  const changeSelectedLocation = (location: RecommendedLocation | null) => {
    setSelectedLocation(location);
  };

  return { selectedLocation, changeSelectedLocation };
};

export default useSelectedLocation;
