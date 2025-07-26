/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import { startingSpotName } from '@shared/types/startingSpotName';

import * as startSpotName from './startSpotName.styled';

interface StartSpotNameProps {
  nameInfo: startingSpotName;
}

function StartSpotName({ nameInfo }: StartSpotNameProps) {
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
      <div css={startSpotName.dot()}></div>
    </div>
  );
}

export default StartSpotName;
