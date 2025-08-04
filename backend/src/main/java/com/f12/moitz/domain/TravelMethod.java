package com.f12.moitz.domain;

public enum TravelMethod {

    SUBWAY(1, "지하철"),
    BUS(2, "버스"),
    WALK(3, "도보"),
    TRANSFER(4, "환승"),;

    private final int code;
    private final String name;

    TravelMethod(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static TravelMethod from(final int code) {
        for (TravelMethod method : values()) {
            if (method.code == code) {
                return method;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 이동 방법 코드입니다: " + code);
    }

    public static TravelMethod from(final String name) {
        for (TravelMethod method : values()) {
            if (method.name.equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 이동 방법 이름입니다: " + name);
    }

}
