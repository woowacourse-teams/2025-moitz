import { typography } from '@shared/styles/default.styled';

import * as dropdownEmpty from './dropdownEmpty.styled';

function DropdownEmpty() {
  return (
    <li css={[typography.b1, dropdownEmpty.base()]}>
      해당하는 역이름이 없습니다.
    </li>
  );
}

export default DropdownEmpty;
