import Layout from '@shared/components/layout/Layout';
import { flex, typography } from '@shared/styles/default.styled';

import * as fallBackPage from './fallBackPage.styled';

function FallBackPage({ reset }: { reset: () => void }) {
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
          <button onClick={reset} css={fallBackPage.button()}>
            새로고침
          </button>
        </div>
      </div>
    </Layout>
  );
}

export default FallBackPage;
