import { flex, typography } from '@shared/styles/default.styled';

import { recommendedLocation } from '@shared/types/recommendedLocation';

import * as card from './placeCard.styled';

interface PlaceCardProps {
  place: recommendedLocation['places'][number];
}

function PlaceCard({ place }: PlaceCardProps) {
  const handleClick = () => {
    window.open(place.url, '_blank');
  };

  return (
    <button
      css={[flex({ direction: 'column' }), card.container()]}
      onClick={handleClick}
      aria-label={`${place.name} 장소 정보 보기`}
    >
      <div css={card.image()}></div>
      <div
        css={[
          flex({ direction: 'column', justify: 'flex-start', gap: 7 }),
          card.content(),
        ]}
      >
        <span css={[typography.sh2, card.text()]}>{place.name}</span>
        <span css={[typography.c2, card.text()]}>
          🕐 도보 {place.walkingTime}분
        </span>
      </div>
    </button>
  );
}

export default PlaceCard;
