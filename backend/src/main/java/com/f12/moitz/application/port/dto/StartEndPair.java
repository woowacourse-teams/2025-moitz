package com.f12.moitz.application.port.dto;

import com.f12.moitz.domain.Place;

public record StartEndPair(
        Place start,
        Place end
) {
}
