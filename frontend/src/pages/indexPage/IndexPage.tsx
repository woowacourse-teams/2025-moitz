import MeetingForm from '@features/meeting/components/meetingForm/MeetingForm';

import HeaderLogo from '@shared/components/headerLogo/HeaderLogo';
import { flex, grid_padding } from '@shared/styles/default.styled';

import * as indexPage from './indexPage.styled';

function IndexPage() {
  return (
    <div
      css={[
        grid_padding,
        flex({ direction: 'column' }),
        indexPage.container(),
        indexPage.scroll(),
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
