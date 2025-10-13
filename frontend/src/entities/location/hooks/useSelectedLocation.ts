import { useState } from 'react';

import { SelectedLocation } from '@features/recommendation/types/SelectedLocation';

import { RecommendedLocation } from '@entities/location/types/Location';

type useSelectedLocationReturn = {
  selectedLocation: SelectedLocation;
  changeSelectedLocation: (location: RecommendedLocation) => void;
};

const useSelectedLocation = (): useSelectedLocationReturn => {
  const [selectedLocation, setSelectedLocation] =
    useState<SelectedLocation>(null);

  const changeSelectedLocation = (location: SelectedLocation) => {
    setSelectedLocation((prevLocation) => {
      if (prevLocation === location) {
        return null;
      }
      return location;
    });
  };

  return { selectedLocation, changeSelectedLocation };
};

export default useSelectedLocation;
