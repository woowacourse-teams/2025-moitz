/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import { startingSpotName } from '@shared/types/startingSpotName';

import * as startingSpotNameStyled from './startingSpotName.styled';

interface StartSpotNameProps {
  nameInfo: startingSpotName;
  isLast: boolean;
}

function StartingSpotName({ nameInfo, isLast }: StartSpotNameProps) {
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
      <div css={!isLast && startingSpotNameStyled.dot()}></div>
    </div>
  );
}

export default StartingSpotName;
