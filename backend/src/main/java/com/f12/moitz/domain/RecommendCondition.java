package com.f12.moitz.domain;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum RecommendCondition {

    CHAT("CHAT", "떠들고 놀기 좋은", "모임"),
    MEETING("MEETING", "회의하기 좋은", "회의"),
    FOCUS("FOCUS", "집중하기 좋은", "스터디"),
    DATE("DATE", "데이트하기 좋은", "분위기 좋은"),
    NOT_SELECTED("NOT_SELECTED", "선택하지 않음", "맛집"),
    ;

    private final String title;
    private final String description;
    private final String keyword;

    RecommendCondition(final String title, final String description, final String keyword) {
        this.title = title;
        this.description = description;
        this.keyword = keyword;
    }

    public static RecommendCondition fromTitle(final String title) {
        return Arrays.stream(values())
                .filter(recommendCondition -> recommendCondition.title.equals(title))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_DESCRIPTION));
    }

}
