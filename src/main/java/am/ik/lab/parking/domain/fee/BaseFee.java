package am.ik.lab.parking.domain.fee;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 基本料金 (単位時間当たりの料金)
 */
public class BaseFee {

    /**
     * 単位時間
     */
    private final Duration unitTime;

    /**
     * 単位時間当たりの金額
     */
    private final MonetaryAmount amount;

    public static BaseFee of(Duration unitTime, MonetaryAmount amount) {
        return new BaseFee(unitTime, amount);
    }

    private BaseFee(Duration unitTime, MonetaryAmount amount) {
        this.unitTime = unitTime;
        this.amount = amount;
    }

    /**
     * 基本料金 * 駐車時間を計算する
     *
     * @param duration 駐車時間
     * @return 料金
     */
    public MonetaryAmount fee(Duration duration) {
        Objects.requireNonNull(duration, "'duration' must not be null");
        if (duration.isNegative()) {
            throw new IllegalArgumentException("'duration' must be greater than 0");
        }
        final long minutes = duration.toMinutes();
        // 単位時間(分)で切り上げ
        return this.amount.multiply(Math.ceil((double) minutes / this.unitTime.toMinutes()));
    }

    public Duration getUnitTime() {
        return this.unitTime;
    }

    public MonetaryAmount getAmount() {
        return this.amount;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", BaseFee.class.getSimpleName() + "[", "]")
            .add("unitTime=" + unitTime)
            .add("amount=" + amount)
            .toString();
    }
}
