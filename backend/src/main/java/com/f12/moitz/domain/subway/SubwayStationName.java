package com.f12.moitz.domain.subway;

import java.util.List;
import lombok.Getter;

@Getter
public class SubwayStationName {

    private static final String ISU = "이수역";
    private static final String CHONGSHIN_UNIVERSITY = "총신대입구역";
    private static final String CHONGSHIN_UNIVERSITY_ISU = "총신대입구(이수)역";

    private final String value;

    public SubwayStationName(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("지하철역 이름은 비어있거나 null일 수 없습니다.");
        }
    }

    public List<String> getSearchNames() {
        if (isIsuAlias()) {
            return List.of(CHONGSHIN_UNIVERSITY_ISU, value);
        }
        return List.of(value);
    }

    private boolean isIsuAlias() {
        return ISU.equals(value) || CHONGSHIN_UNIVERSITY.equals(value);
    }

}
