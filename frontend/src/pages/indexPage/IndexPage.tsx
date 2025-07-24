import PlanForm from '@widgets/meeting/components/planForm/PlanForm';

import HeaderLogo from '@shared/components/headerLogo/headerLogo';
import { gridPadding, flex } from '@shared/styles/default.styled';

import * as indexPage from './indexPage.styled';

function IndexPage() {
  return (
    <div
      css={[gridPadding, flex({ direction: 'column' }), indexPage.container()]}
    >
      <div css={indexPage.headerLogo()}>
        <HeaderLogo />
      </div>
      <PlanForm />
    </div>
  );
}

export default IndexPage;
