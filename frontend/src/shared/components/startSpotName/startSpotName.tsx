/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import { nameInfo } from '@shared/types/nameInfo';

import * as startSpotName from './startSpotName.styled';

interface StartSpotNameProps {
  nameInfo: nameInfo;
  isLast?: boolean;
}

function StartSpotName({ nameInfo, isLast = false }: StartSpotNameProps) {
  return (
    <div
      key={nameInfo.index}
      css={flex({
        justify: 'center',
        align: 'center',
        gap: 5,
      })}
    >
      <span css={[typography.b2, startSpotName.nameList()]}>
        {nameInfo.name}
      </span>
      {!isLast && <div css={startSpotName.dot()}></div>}
    </div>
  );
}

export default StartSpotName;
