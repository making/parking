package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * 時間帯最大料金<br>
 * 指定された時間帯を過ぎると課金されるもの
 * https://times-info.net/info/max_fee/#time
 */
public class MaxPerTimePeriodFeeStrategy extends TimePeriodFeeStrategy {

    private final MonetaryAmount maxAmountInDaytime;

    private final MonetaryAmount maxAmountInNight;

    public MaxPerTimePeriodFeeStrategy(LocalTime daytimeBegin, LocalTime daytimeEnd, BaseFee daytimeFee, BaseFee nightFee, MonetaryAmount maxAmountInDaytime, MonetaryAmount maxAmountInNight) {
        super(daytimeBegin, daytimeEnd, daytimeFee, nightFee);
        this.maxAmountInDaytime = Objects.requireNonNullElse(maxAmountInDaytime, Jpy.INFINITE);
        this.maxAmountInNight = Objects.requireNonNullElse(maxAmountInNight, Jpy.INFINITE);
    }

    public static Supplier partial(MonetaryAmount maxAmountInDaytime, MonetaryAmount maxAmountInNight) {
        return (daytimeBegin, daytimeEnd, daytimeFee, nightFee)
            -> new MaxPerTimePeriodFeeStrategy(daytimeBegin, daytimeEnd, daytimeFee, nightFee, maxAmountInDaytime, maxAmountInNight);
    }

    @Override
    public MonetaryAmount calcFee(LocalDateTime in, LocalDateTime out) {
        return this.calcRegularFee(in, out);
    }

    @Override
    protected MonetaryAmount adjustFeeOfCurrentPeriod(MonetaryAmount amount, BaseFee baseFee) {
        if (baseFee == super.daytimeFee) {
            //　昼時間帯
            if (amount.isGreaterThan(this.maxAmountInDaytime)) {
                return this.maxAmountInDaytime;
            } else {
                return amount;
            }
        }
        // 夜時間帯
        if (amount.isGreaterThan(this.maxAmountInNight)) {
            return this.maxAmountInNight;
        } else {
            return amount;
        }
    }
}
