import { Link } from 'react-router';

import { flex, typography } from '@shared/styles/default.styled';

import * as notFoundPage from './notFoundPage.styled';

function NotFoundPage() {
  return (
    <div
      css={[
        flex({
          direction: 'column',
          justify: 'center',
          align: 'center',
          gap: 40,
        }),
        notFoundPage.base,
      ]}
    >
      <p css={typography.h1}>죄송합니다. 페이지를 사용할 수 없습니다.</p>
      <div
        css={flex({
          direction: 'column',
          justify: 'center',
          align: 'center',
        })}
      >
        <span css={typography.b1}>
          클릭하신 링크가 잘못되었거나 페이지가 삭제되었습니다.
        </span>
        <Link to="/">모잇지로 돌아가기.</Link>
      </div>
    </div>
  );
}

export default NotFoundPage;
