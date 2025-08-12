import { typography } from '@shared/styles/default.styled';

import * as dropdownList from './dropdownList.styled';

interface DropdownListProps {
  stations: string[];
  handleStationSelect: (station: string) => void;
}

function DropdownList({ stations, handleStationSelect }: DropdownListProps) {
  return (
    <>
      {stations.map((station) => (
        <li
          key={station}
          css={[typography.b1, dropdownList.base()]}
          onClick={() => handleStationSelect(station)}
        >
          {station}
        </li>
      ))}
    </>
  );
}

export default DropdownList;
