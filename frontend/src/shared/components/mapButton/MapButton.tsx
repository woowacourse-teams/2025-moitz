import * as mapButton from './mapButton.styled';

interface MapButtonProps {
  src: string;
  alt: string;
}

function MapButton({ src, alt }: MapButtonProps) {
  return (
    <button css={mapButton.base()}>
      <img src={src} alt={alt} />
    </button>
  );
}

export default MapButton;
