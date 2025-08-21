import FallBackPage from '@pages/components/fallBackPage/FallBackPage';

import ProgressLoading from '@features/loading/components/progressLoading/ProgressLoading';
import MeetingForm from '@features/meeting/components/meetingForm/MeetingForm';

import { useLocationsContext } from '@entities/location/contexts/useLocationsContext';

import HeaderLogo from '@shared/components/headerLogo/HeaderLogo';
import { flex, grid_padding, scroll } from '@shared/styles/default.styled';

import * as indexPage from './indexPage.styled';

function IndexPage() {
  const { isLoading, isError, errorMessage } = useLocationsContext();

  if (isLoading) return <ProgressLoading />;
  if (isError)
    return <FallBackPage reset={() => {}} error={new Error(errorMessage)} />;
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
