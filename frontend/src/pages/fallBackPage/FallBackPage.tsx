import BottomButton from '@shared/components/bottomButton/BottomButton';
import Layout from '@shared/components/layout/Layout';
import { flex, typography } from '@shared/styles/default.styled';

import IconError from '@icons/icon-error.svg';

import * as fallBackPage from './fallBackPage.styled';

interface FallBackPageProps {
  reset: () => void;
  error: Error | null;
  text?: string;
}

function FallBackPage({ reset, error, text }: FallBackPageProps) {
  return (
    <Layout>
      <div
        role="alert"
        css={[
          flex({
            direction: 'column',
            justify: 'space-between',
            align: 'center',
            gap: 40,
          }),
          fallBackPage.base(),
        ]}
      >
        <div
          css={[
            flex({
              direction: 'column',
              justify: 'center',
              align: 'center',
              gap: 30,
            }),
            fallBackPage.content(),
          ]}
        >
          <img src={IconError} alt="error" css={fallBackPage.errorIcon()} />
          <p css={typography.h1}>일시적인 오류가 발생했어요</p>
          <span css={[typography.b1, fallBackPage.description()]}>
            잠시 후 다시 시도해 주시겠어요?
            <br />
            계속 문제가 발생한다면 모잇지에 알려주세요!
            <br />
            {error?.message || '알 수 없는 오류가 발생했습니다'}
          </span>
        </div>
        <BottomButton
          type="button"
          text={text || '홈으로 가기'}
          active
          onClick={reset}
        />
      </div>
    </Layout>
  );
}

export default FallBackPage;
