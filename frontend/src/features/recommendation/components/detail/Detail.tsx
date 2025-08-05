import { flex, typography } from '@shared/styles/default.styled';

import { recommendedLocation } from '@shared/types/recommendedLocation';

import DetailSection from '../detailSection/DetailSection';
import PlaceCard from '../placeCard/PlaceCard';

import * as detail from './detail.styled';

interface DetailProps {
  selectedLocation: recommendedLocation;
}

function Detail({ selectedLocation }: DetailProps) {
  return (
    <div css={flex({ direction: 'column', gap: 30 })}>
      <DetailSection
        isHeader={true}
        title={selectedLocation.name}
        isBestBadge={selectedLocation.isBest}
      >
        <div css={detail.reason()}>
          <p css={[typography.b2, detail.reasonText()]}>
            {selectedLocation.reason}
          </p>
        </div>
      </DetailSection>

      <DetailSection
        isHeader={false}
        title={'각 출발지로부터 이동 방법'}
        isBestBadge={false}
      >
        <></>
      </DetailSection>

      <DetailSection isHeader={false} title={'추천 장소'} isBestBadge={false}>
        <div css={[flex(), detail.placeList()]}>
          {selectedLocation.places.map((place) => (
            <PlaceCard key={place.index} place={place} />
          ))}
        </div>
      </DetailSection>
    </div>
  );
}

export default Detail;
