import { flex } from '@shared/styles/default.styled';

import * as mapButton from './mapButton.styled';

interface MapButtonProps {
  src: string;
  alt: string;
  onClick?: () => void;
}

function MapButton({ src, alt, onClick }: MapButtonProps) {
  return (
    <button
      css={[flex({ justify: 'center', align: 'center' }), mapButton.base()]}
      onClick={onClick}
    >
      <img src={src} alt={alt} />
    </button>
  );
}

export default MapButton;
