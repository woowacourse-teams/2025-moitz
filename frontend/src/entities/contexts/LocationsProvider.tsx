import React from 'react';

import useLocations from '@entities/hooks/useLocations';

import LocationsContext from './LocationsContext';

export const LocationsProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const locations = useLocations();
  return (
    <LocationsContext.Provider value={locations}>
      {children}
    </LocationsContext.Provider>
  );
};
