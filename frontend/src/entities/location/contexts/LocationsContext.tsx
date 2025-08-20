import { createContext } from 'react';

import { useLocationsReturn } from '@entities/location/hooks/useLocations';

const LocationsContext = createContext<useLocationsReturn | null>(null);

export default LocationsContext;
