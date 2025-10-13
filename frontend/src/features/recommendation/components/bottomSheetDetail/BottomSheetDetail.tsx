import { SelectedLocation } from '@features/recommendation/types/SelectedLocation';

import { StartingPlace } from '@entities/location/types/Location';

import { numberToCharCode } from '@shared/lib/numberToCharCode';
import { flex, scroll, typography } from '@shared/styles/default.styled';

import DetailSection from '../detailSection/DetailSection';
import PlaceCard from '../placeCard/PlaceCard';
import RouteCard from '../routeCard/RouteCard';

import * as bottomSheetDetail from './bottomSheetDetail.styled';

interface BottomSheetDetailProps {
  startingPlaces: StartingPlace[];
  selectedLocation: SelectedLocation;
}

function BottomSheetDetail({
  startingPlaces,
  selectedLocation,
}: BottomSheetDetailProps) {
  return (
    <div
      css={[
        flex({ direction: 'column', gap: 30 }),
        bottomSheetDetail.container(),
      ]}
    >
      <DetailSection
        isHeader={true}
        title={selectedLocation.name}
        isBestBadge={selectedLocation.isBest}
      >
        <div css={bottomSheetDetail.reason()}>
          <p css={[typography.b2, bottomSheetDetail.reasonText()]}>
            {selectedLocation.reason}
          </p>
        </div>
      </DetailSection>

      <DetailSection
        isHeader={false}
        title={'각 출발지로부터 이동 방법'}
        isBestBadge={false}
      >
        <div css={flex({ direction: 'column', gap: 20 })}>
          {selectedLocation.routes.map((route) => (
            <RouteCard
              key={route.startingPlaceId}
              startingPlaceIndex={numberToCharCode(route.startingPlaceId)}
              startingPlaceName={
                startingPlaces.find(
                  (place) => place.id === route.startingPlaceId,
                )?.name || ''
              }
              route={route}
            />
          ))}
        </div>
      </DetailSection>

      <DetailSection isHeader={false} title={'추천 장소'} isBestBadge={false}>
        <div css={[flex(), scroll, bottomSheetDetail.placeList()]}>
          {selectedLocation.places.map((place) => (
            <PlaceCard key={place.index} place={place} />
          ))}
        </div>
      </DetailSection>
    </div>
  );
}

export default BottomSheetDetail;
