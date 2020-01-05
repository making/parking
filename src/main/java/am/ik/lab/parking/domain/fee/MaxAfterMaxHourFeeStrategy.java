package am.ik.lab.parking.domain.fee;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 最大料金 駐車後X時間<br>
 * 一定時間内に限った最大料金。時間を超えると課金されるもの
 * https://times-info.net/info/max_fee/#max
 */
public class MaxAfterMaxHourFeeStrategy extends TimePeriodFeeStrategy {

    /**
     * 駐車後X時間のX
     */
    private final Duration maxDuration;

    /**
     * 最大料金
     */
    private final MonetaryAmount maxAmount;

    public MaxAfterMaxHourFeeStrategy(LocalTime daytimeBegin, LocalTime daytimeEnd,
                                      BaseFee daytimeFee, BaseFee nightFee,
                                      Duration maxDuration, MonetaryAmount maxAmount) {
        super(daytimeBegin, daytimeEnd, daytimeFee, nightFee);
        this.maxDuration = maxDuration;
        this.maxAmount = maxAmount;
    }

    public static Supplier partial(Duration maxDuration, MonetaryAmount maxAmount) {
        return (daytimeBegin, daytimeEnd, daytimeFee, nightFee)
            -> new MaxAfterMaxHourFeeStrategy(daytimeBegin, daytimeEnd, daytimeFee, nightFee, maxDuration, maxAmount);
    }

    @Override
    public MonetaryAmount calcFee(LocalDateTime in, LocalDateTime out) {
        if (Duration.between(in, out).compareTo(this.maxDuration) > 0) {
            final LocalDateTime beginOfSecond = in.plus(this.maxDuration);
            final MonetaryAmount second = this.calcFee(beginOfSecond, out);
            return this.maxAmount.add(second);
        }

        final MonetaryAmount regularFee = super.calcRegularFee(in, out);
        if (regularFee.isGreaterThan(this.maxAmount)) {
            return this.maxAmount;
        }
        return regularFee;
    }
}
