import Logo from '@shared/components/logo/Logo';
import { flex, typography } from '@shared/styles/default.styled';

import * as headerLogo from './headerLogo.styled';

function HeaderLogo() {
  return (
    <div
      css={flex({
        direction: 'column',
        justify: 'center',
        align: 'center',
        gap: 10,
      })}
    >
      <Logo />
      <span css={[typography.c1, headerLogo.tagline()]}>
        친구들과 모임, 만날 지역을 빠르게!
      </span>
    </div>
  );
}

export default HeaderLogo;
