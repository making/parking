package am.ik.lab.parking.domain.fee;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 当日24時まで最大料金<br>
 * 日付が変わると同時に課金されるもの
 * https://times-info.net/info/max_fee/#today
 */
public class MaxPerDayFeeStrategy extends TimePeriodFeeStrategy {

    /**
     * 当日最大料金
     */
    private final MonetaryAmount maxAmountPerDay;

    public MaxPerDayFeeStrategy(LocalTime daytimeBegin, LocalTime daytimeEnd, BaseFee daytimeFee, BaseFee nightFee, MonetaryAmount maxAmountPerDay) {
        super(daytimeBegin, daytimeEnd, daytimeFee, nightFee);
        this.maxAmountPerDay = maxAmountPerDay;
    }

    public static Supplier partial(MonetaryAmount maxAmountPerDay) {
        return (daytimeBegin, daytimeEnd, daytimeFee, nightFee)
            -> new MaxPerDayFeeStrategy(daytimeBegin, daytimeEnd, daytimeFee, nightFee, maxAmountPerDay);
    }

    @Override
    public MonetaryAmount calcFee(LocalDateTime in, LocalDateTime out) {
        if (!this.isOverMidnight(in, out)) {
            final MonetaryAmount baseFee = super.calcRegularFee(in, out);
            if (baseFee.isGreaterThan(this.maxAmountPerDay)) {
                return this.maxAmountPerDay;
            }
            return baseFee;
        }

        final LocalDateTime endOfFirst = in.plusDays(1).with(LocalTime.of(0, 0));
        final MonetaryAmount first = super.calcRegularFee(in, endOfFirst);
        final MonetaryAmount second = this.calcFee(endOfFirst, out);

        if (first.isGreaterThan(this.maxAmountPerDay)) {
            return this.maxAmountPerDay.add(second);
        }
        return first.add(second);
    }

    boolean isOverMidnight(LocalDateTime in, LocalDateTime out) {
        return out.getDayOfMonth() > in.getDayOfMonth();
    }
}
