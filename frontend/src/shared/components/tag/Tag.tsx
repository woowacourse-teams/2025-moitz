/** @jsxImportSource @emotion/react */

import { flex, inline_flex, typography } from '@shared/styles/default.styled';

import IconCancel from '@icons/icon-cancel.svg';

import * as tag from './tag.styled';

interface TagProps {
  text: string;
  onClick?: () => void;
}

function Tag({ text, onClick }: TagProps) {
  return (
    <div
      css={[
        inline_flex({ justify: 'center', align: 'center', gap: 4 }),
        tag.base(),
      ]}
    >
      <span css={typography.b2}>{text}</span>
      <button
        css={[flex({ justify: 'center', align: 'center' }), tag.button()]}
        onClick={onClick}
        type="button"
      >
        <img src={IconCancel} alt="icon-cancel"></img>
      </button>
    </div>
  );
}

export default Tag;
