/** @jsxImportSource @emotion/react */

import { flex, typography } from '@shared/styles/default.styled';

import { nameInfo } from '@shared/types/nameInfo';

import StartSpotName from '../startSpotName/startSpotName';

import * as startSpotWrapper from './startSpotWrapper.styled';

interface StartSpotWrapperProps {
  nameList: nameInfo[];
}

function StartSpotWrapper({ nameList }: StartSpotWrapperProps) {
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

export default StartSpotWrapper;
