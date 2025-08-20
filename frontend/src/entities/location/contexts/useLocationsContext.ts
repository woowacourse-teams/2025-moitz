import { useContext } from 'react';

import LocationsContext from './LocationsContext';

export const useLocationsContext = () => {
  const context = useContext(LocationsContext);
  if (!context) {
    throw new Error(
      'useLocationsContext는 LocationsProvider 안에 있어야 합니다.',
    );
  }
  return context;
};
