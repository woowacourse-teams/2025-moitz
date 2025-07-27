/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import { startingSpotName } from '@shared/types/startingSpotName';

import StartSpotName from '../startingSpotName/StartingSpotName';

import * as startSpotWrapper from './startingSpotWrapper.styled';

interface StaringSpotWrapperProps {
  nameList: startingSpotName[];
}

function StartingSpotWrapper({ nameList }: StaringSpotWrapperProps) {
  return (
    <div css={[flex({ align: 'center', gap: 10 }), startSpotWrapper.base()]}>
      <span css={[typography.sh1, startSpotWrapper.title()]}>출발지</span>
      <div css={[flex({ wrap: 'wrap', gap: 5 })]}>
        {nameList.map((nameInfo) => (
          <StartSpotName key={nameInfo.index} nameInfo={nameInfo} />
        ))}
      </div>
    </div>
  );
}

export default StartingSpotWrapper;
