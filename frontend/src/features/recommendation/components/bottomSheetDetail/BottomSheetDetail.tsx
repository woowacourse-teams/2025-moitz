import React from 'react';

import { SelectedLocation } from '@features/recommendation/types/SelectedLocation';

import { StartingPlace } from '@entities/location/types/Location';

import { flex } from '@shared/styles/default.styled';

import DetailSectionInfo from '../detailSectionInfo/DetailSectionInfo';
import DetailSectionPlace from '../detailSectionPlace/DetailSectionPlace';
import DetailSectionRoute from '../detailSectionRoute/DetailSectionRoute';

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
      <DetailSectionInfo selectedLocation={selectedLocation} />

      <DetailSectionPlace selectedLocation={selectedLocation} />

      <DetailSectionRoute
        startingPlaces={startingPlaces}
        selectedLocation={selectedLocation}
      />
    </div>
  );
}

export default React.memo(BottomSheetDetail);
