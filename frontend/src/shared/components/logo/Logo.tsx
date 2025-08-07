import { flex } from '@shared/styles/default.styled';

import IconLogo from '@icons/logo-icon.svg';
import IconLogoText from '@icons/logo-text.svg';

function Logo() {
  return (
    <div css={[flex({ justify: 'center', align: 'center', gap: 5 })]}>
      <img src={IconLogo} alt="logo-icon" />
      <img src={IconLogoText} alt="logo-text" />
    </div>
  );
}

export default Logo;
