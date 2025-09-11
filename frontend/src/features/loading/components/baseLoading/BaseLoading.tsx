import React, { memo } from 'react';

import Logo from '@shared/components/logo/Logo';
import { flex } from '@shared/styles/default.styled';

import * as loading from './BaseLoading.styled';

interface BaseLoadingProps {
  children?: React.ReactNode;
}

const StaticContent = memo(() => (
  <div css={flex({ justify: 'center', align: 'center', gap: 10 })}>
    <Logo type="white" />
  </div>
));

StaticContent.displayName = 'StaticContent';

function BaseLoading({ children }: BaseLoadingProps) {
  return (
    <div
      css={[
        flex({
          direction: 'column',
          justify: 'center',
          align: 'center',
          gap: 10
        }),
        loading.container()
      ]}
    >
      <StaticContent />
      {children}
    </div>
  );
}

export default BaseLoading;
