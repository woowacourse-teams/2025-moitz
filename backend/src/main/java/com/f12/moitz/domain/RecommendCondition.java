package com.f12.moitz.domain;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum RecommendCondition {

    CAFE("CAFE", "카페"),
    RESTAURANT("RESTAURANT","식당"),
    BAR("BAR","술집"),
    STUDY_CAFE("STUDY_CAFE", "스터디카페"),
    SPACE_RENTAL("SPACE_RENTAL", "공간대여"),
    PC_ROOM("PC_ROOM", "PC방"),
    KARAOKE("KARAOKE", "노래방"),
    ACTIVITY("ACTIVITY", "클라이밍,볼링,사격,당구"),
    ENTERTAINMENT("ENTERTAINMENT", "방탈출,만화방,보드게임카페,영화관"),
    NOT_SELECTED("NOT_SELECTED", "맛집");

    private final String title;
    private final String keyword;

    RecommendCondition(final String title, final String keyword) {
        this.title = title;
        this.keyword = keyword;
    }

    public static RecommendCondition fromTitle(final String title) {
        return Arrays.stream(values())
                .filter(recommendCondition -> recommendCondition.title.equals(title))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_DESCRIPTION, title));
    }

}
