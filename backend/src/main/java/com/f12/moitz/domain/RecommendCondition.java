package com.f12.moitz.domain;

import com.f12.moitz.common.error.exception.BadRequestException;
import com.f12.moitz.common.error.exception.GeneralErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum RecommendCondition {

    CHAT("CHAT", "떠들고 놀기 좋은", List.of("회식")),
    MEETING("MEETING", "회의하기 좋은", List.of("카페", "공유 오피스")),
    FOCUS("FOCUS", "집중하기 좋은", List.of("카페")),
    DATE("DATE", "데이트하기 좋은", List.of("양식", "카페")),
    NOT_SELECTED("NOT_SELECTED", "선택하지 않음", List.of()),
    ;

    private final String title;
    private final String description;
    private final List<String> categories;

    RecommendCondition(final String title, final String description, final List<String> categories) {
        this.title = title;
        this.description = description;
        this.categories = categories;
    }

    public static RecommendCondition fromTitle(final String title) {
        return Arrays.stream(values())
                .filter(recommendCondition -> recommendCondition.title.equals(title))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(GeneralErrorCode.INPUT_INVALID_DESCRIPTION));
    }

    public String getCategoryNames() {
        return String.join(", ", categories);
    }

}
