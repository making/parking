package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;

import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

class CompositeFeeStrategyTest extends TimePeriodFeeStrategyTest {

    @Override
    TimePeriodFeeStrategy.Supplier supplier() {
        return (daytimeBegin, daytimeEnd, daytimeFee, nightFee) -> {
            final SortedMap<LocalDateTime, FeeStrategy> strategies = new TreeMap<>();
            strategies.put(LocalDateTime.of(2019, 4, 2, 0, 0).with(daytimeEnd).minusHours(1),
                new MaxPerDayFeeStrategy(daytimeBegin, daytimeEnd, daytimeFee, nightFee, Jpy.of(3000)));
            strategies.put(LocalDateTime.MAX, new MaxPerDayFeeStrategy(daytimeBegin, daytimeEnd, daytimeFee, nightFee, Jpy.INFINITE));
            return new CompositeFeeStrategy(strategies);
        };
    }
}