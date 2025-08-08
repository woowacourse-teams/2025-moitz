import { flex, shadow } from '@shared/styles/default.styled';

import * as mapButton from './mapButton.styled';

interface MapButtonProps {
  src: string;
  alt: string;
}

function MapButton({ src, alt }: MapButtonProps) {
  return (
    <button
      css={[
        flex({ justify: 'center', align: 'center' }),
        shadow.map,
        mapButton.base(),
      ]}
    >
      <img src={src} alt={alt} />
    </button>
  );
}

export default MapButton;
