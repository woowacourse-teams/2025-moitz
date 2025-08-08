import React from 'react';

import Logo from '@shared/components/logo/Logo';
import { flex } from '@shared/styles/default.styled';

import * as loading from './BaseLoading.styled';

interface BaseLoadingProps {
  children?: React.ReactNode;
}

function BaseLoading({ children }: BaseLoadingProps) {
  return (
    <div
      css={[
        flex({
          direction: 'column',
          justify: 'center',
          align: 'center',
          gap: 10,
        }),
        loading.container(),
      ]}
    >
      <div css={flex({ justify: 'center', align: 'center', gap: 10 })}>
        <Logo type="white" />
      </div>
      {children}
    </div>
  );
}

export default BaseLoading;
