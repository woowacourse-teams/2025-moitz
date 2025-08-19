package com.f12.moitz.infrastructure.client.open.dto;

public record PathResponse(
        StationResponse dptreStn,
        StationResponse arvlStn,
        int stnSctnDstc,
        int reqHr,
        int wtngHr,
        String upbdnbSe,
        String trainDptreTm,
        String trainArvlTm,
        String trsitYn,
        String etrnYn,
        String nonstopYn
) {
    public boolean isTransfer() {
        return "Y".equals(trsitYn);
    }
}
