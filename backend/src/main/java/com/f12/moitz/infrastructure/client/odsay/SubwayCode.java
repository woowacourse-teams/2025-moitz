package com.f12.moitz.infrastructure.client.odsay;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum SubwayCode {

    SEOUL_METRO_LINE1("1호선", 1),
    SEOUL_METRO_LINE2("2호선", 2),
    SEOUL_METRO_LINE3("3호선", 3),
    SEOUL_METRO_LINE4("4호선", 4),
    SEOUL_METRO_LINE5("5호선", 5),
    SEOUL_METRO_LINE6("6호선", 6),
    SEOUL_METRO_LINE7("7호선", 7),
    SEOUL_METRO_LINE8("8호선", 8),
    SEOUL_METRO_LINE9("9호선", 9),
    GTX_A("GTX_A", 91),
    AIRPORT_RAILROAD("공항철도", 101),
    GYEONGUI_JOUNGANG("경의중앙선", 104),
    EVERLINE("용인 에버라인", 107),
    GYEONGCHUN("경춘선", 108),
    SIN_BUNDANG("신분당선", 109),
    UIJEONGBU_LRT("의정부 경전철", 110),
    GYEONGGANG("경강선", 112),
    WOOE_SINSEUL_LRT("우이 신설선", 113),
    SEOHAE_LINE("서해선", 114),
    GIMPO_LRT("김포골드라인", 115),
    SUIN_BUNDANG("수인분당선", 116),
    SILLIM_LRT("신림선", 117),
    INCHEON1("인천 1호선", 21),
    INCHEON2("인천 2호선", 22);

    private final String title;
    private final int code;

    SubwayCode(final String title, final int code) {
        this.title = title;
        this.code = code;
    }

    public static SubwayCode fromCode(final int code) {
        return Arrays.stream(values())
                .filter(subwayCode -> subwayCode.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("코드에 해당하는 지하철 노선 정보가 존재하지 않습니다."));
    }

}
