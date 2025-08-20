import MeetingForm from '@features/meeting/components/meetingForm/MeetingForm';

import HeaderLogo from '@shared/components/headerLogo/HeaderLogo';
import { flex, grid_padding, scroll } from '@shared/styles/default.styled';

import * as indexPage from './indexPage.styled';

function IndexPage() {
  return (
    <div
      css={[
        flex({ direction: 'column' }),
        grid_padding,
        scroll,
        indexPage.base(),
      ]}
    >
      <div css={indexPage.headerLogo()}>
        <HeaderLogo />
      </div>
      <MeetingForm />
    </div>
  );
}

export default IndexPage;
