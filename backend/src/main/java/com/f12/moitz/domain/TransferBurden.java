package com.f12.moitz.domain;

import java.util.Comparator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class TransferBurden implements Comparable<TransferBurden> {

    private final double averageTransferCount;
    private final int maxTransferCount;
    private final int transferDiff;

    public TransferBurden(
            final double averageTransferCount,
            final int maxTransferCount,
            final int transferDiff
    ) {
        validate(averageTransferCount, maxTransferCount, transferDiff);
        this.averageTransferCount = averageTransferCount;
        this.maxTransferCount = maxTransferCount;
        this.transferDiff = transferDiff;
    }

    private void validate(
            final double averageTransferCount,
            final int maxTransferCount,
            final int transferDiff
    ) {
        if (Double.isNaN(averageTransferCount) || averageTransferCount < 0) {
            throw new IllegalArgumentException("평균 환승 횟수는 음수이거나 NaN일 수 없습니다.");
        }
        if (maxTransferCount < 0) {
            throw new IllegalArgumentException("최대 환승 횟수는 음수일 수 없습니다.");
        }
        if (transferDiff < 0) {
            throw new IllegalArgumentException("환승 횟수 차이는 음수일 수 없습니다.");
        }
    }

    @Override
    public int compareTo(final TransferBurden other) {
        return Comparator.comparingDouble(TransferBurden::getAverageTransferCount)
                .thenComparingInt(TransferBurden::getMaxTransferCount)
                .thenComparingInt(TransferBurden::getTransferDiff)
                .compare(this, other);
    }

}
