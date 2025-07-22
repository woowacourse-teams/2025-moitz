/** @jsxImportSource @emotion/react */
import IconCancel from '../../../../assets/icon/icon-cancel.svg';
import { inline_flex, flex, typography } from '../../styles/default.styled';

import * as tag from './tag.styled';

interface TagProps {
  text: string;
}

function Tag({ text }: TagProps) {
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
      >
        <img src={IconCancel} alt="icon-cancel"></img>
      </button>
    </div>
  );
}

export default Tag;
