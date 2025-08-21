import BottomButton from '@shared/components/bottomButton/BottomButton';
import Layout from '@shared/components/layout/Layout';
import { flex, typography } from '@shared/styles/default.styled';

import * as fallBackPage from './fallBackPage.styled';

function FallBackPage({
  reset,
  error,
}: {
  reset: () => void;
  error: Error | null;
}) {
  return (
    <Layout>
      <div
        role="alert"
        css={[
          flex({
            direction: 'column',
            justify: 'center',
            align: 'center',
            gap: 40,
          }),
          fallBackPage.base(),
        ]}
      >
        <p css={typography.h1}>앱에 문제가 발생했어요.</p>
        <div
          css={flex({
            direction: 'column',
            justify: 'center',
            align: 'center',
          })}
        >
          {error.message}
          <BottomButton onClick={reset} type="button" text="새로고침" active />
        </div>
      </div>
    </Layout>
  );
}

export default FallBackPage;
