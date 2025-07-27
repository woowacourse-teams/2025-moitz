/** @jsxImportSource @emotion/react */

import StartingSpotName from '@shared/components/startingSpotName/StartingSpotName';
import { flex, typography } from '@shared/styles/default.styled';

import { startingSpotName } from '@shared/types/startingSpotName';

import * as startSpotWrapper from './startingSpotWrapper.styled';

interface StaringSpotWrapperProps {
  startingSpotNameList: startingSpotName[];
}

function StartingSpotWrapper({
  startingSpotNameList,
}: StaringSpotWrapperProps) {
  return (
    <div css={[flex({ align: 'center', gap: 10 }), startSpotWrapper.base()]}>
      <span css={[typography.sh1, startSpotWrapper.title()]}>출발지</span>
      <div css={[flex({ wrap: 'wrap', gap: 5 })]}>
        {startingSpotNameList.map((nameInfo) => (
          <StartingSpotName key={nameInfo.index} nameInfo={nameInfo} />
        ))}
      </div>
    </div>
  );
}

export default StartingSpotWrapper;
