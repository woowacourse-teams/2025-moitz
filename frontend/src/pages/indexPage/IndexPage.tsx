import InputForm from '@features/meeting/components/inputForm/InputForm';

import HeaderLogo from '@shared/components/headerLogo/HeaderLogo';
import { flex, gridPadding } from '@shared/styles/default.styled';

import * as indexPage from './indexPage.styled';

function IndexPage() {
  return (
    <div
      css={[
        gridPadding,
        flex({ direction: 'column' }),
        indexPage.container(),
        indexPage.scroll(),
      ]}
    >
      <div css={indexPage.headerLogo()}>
        <HeaderLogo />
      </div>
      <InputForm />
    </div>
  );
}

export default IndexPage;
