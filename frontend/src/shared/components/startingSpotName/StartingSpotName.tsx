/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import { startingSpotName } from '@shared/types/startingSpotName';

import * as startingSpotNameStyled from './startingSpotName.styled';

interface StartSpotNameProps {
  nameInfo: startingSpotName;
}

function StartingSpotName({ nameInfo }: StartSpotNameProps) {
  return (
    <div
      key={nameInfo.index}
      css={flex({
        justify: 'center',
        align: 'center',
        gap: 5,
      })}
    >
      <span css={[typography.b2, startingSpotNameStyled.nameList()]}>
        {nameInfo.name}
      </span>
      <div css={startingSpotNameStyled.dot()}></div>
    </div>
  );
}

export default StartingSpotName;
