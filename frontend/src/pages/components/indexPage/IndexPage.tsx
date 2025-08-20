import ProgressLoading from '@features/loading/components/progressLoading/ProgressLoading';
import MeetingForm from '@features/meeting/components/meetingForm/MeetingForm';

import { useLocationsContext } from '@entities/location/contexts/useLocationsContext';

import HeaderLogo from '@shared/components/headerLogo/HeaderLogo';
import { flex, grid_padding, scroll } from '@shared/styles/default.styled';

import * as indexPage from './indexPage.styled';

function IndexPage() {
  const { isLoading } = useLocationsContext();

  if (isLoading) return <ProgressLoading />;
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
