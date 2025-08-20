import { shadow } from '@shared/styles/default.styled';

import * as dropdown from './dropdown.styled';
import DropdownEmpty from './dropdownEmpty/DropdownEmpty';
import DropdownList from './dropdownList/DropdownList';

interface DropdownProps {
  stations: string[];
  handleStationSelect: (station: string) => void;
}

function Dropdown({ stations, handleStationSelect }: DropdownProps) {
  return (
    <ul css={[dropdown.base(), shadow.dropdown]}>
      {!stations.length ? (
        <DropdownEmpty />
      ) : (
        <DropdownList
          stations={stations}
          handleStationSelect={handleStationSelect}
        />
      )}
    </ul>
  );
}

export default Dropdown;
