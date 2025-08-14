import { useState } from 'react';

import { RecommendedLocation } from '../../entities/types/Location';

type useSelectedLocationReturn = {
  selectedLocation: RecommendedLocation;
  changeSelectedLocation: (location: RecommendedLocation) => void;
};

const useSelectedLocation = (): useSelectedLocationReturn => {
  const [selectedLocation, setSelectedLocation] =
    useState<RecommendedLocation>(null);

  const changeSelectedLocation = (location: RecommendedLocation) => {
    setSelectedLocation(location);
  };

  return { selectedLocation, changeSelectedLocation };
};

export default useSelectedLocation;
