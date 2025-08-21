import { useNavigate } from 'react-router';

import BottomButton from '@shared/components/bottomButton/BottomButton';
import { flex, typography } from '@shared/styles/default.styled';

import IconError from '@icons/icon-error.svg';

import * as notFoundPage from './notFoundPage.styled';

function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div
      css={[
        flex({
          direction: 'column',
          justify: 'space-between',
          align: 'center',
          gap: 40,
        }),
        notFoundPage.base(),
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
          notFoundPage.content(),
        ]}
      >
        <img src={IconError} alt="error" css={notFoundPage.errorIcon()} />
        <p css={typography.h1}>앗! 찾으시는 페이지가 없어요</p>
        <span css={[typography.b1, notFoundPage.description()]}>
          주소가 변경되었거나 더는 제공되지 않는 페이지예요.
        </span>
      </div>
      <BottomButton
        type="button"
        text="홈으로 가기"
        active
        onClick={() => navigate('/')}
      />
    </div>
  );
}

export default NotFoundPage;
