import { flex } from '@shared/styles/default.styled';

import IconLogo from '@icons/logo-icon.svg';
import IconLogoTextWhite from '@icons/logo-text-white.svg';
import IconLogoText from '@icons/logo-text.svg';

interface LogoProps {
  type?: 'black' | 'white';
}

function Logo({ type = 'black' }: LogoProps) {
  return (
    <div css={[flex({ justify: 'center', align: 'center', gap: 5 })]}>
      <img src={IconLogo} alt="logo-icon" />
      <img
        src={type === 'black' ? IconLogoText : IconLogoTextWhite}
        alt="logo-text"
      />
    </div>
  );
}

export default Logo;
