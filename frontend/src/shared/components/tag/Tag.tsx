import IconCancel from '../../../../assets/icon/icon-cancel.svg';
import { typography } from '../../styles/default.styled';

import * as tag from './tag.styled';

interface TagProps {
  text: string;
}

function Tag({ text }: TagProps) {
  return (
    <div css={tag.base()}>
      <span css={typography.b2}>{text}</span>
      <button css={tag.button()}>
        <img src={IconCancel} alt="icon-cancel"></img>
      </button>
    </div>
  );
}

export default Tag;
