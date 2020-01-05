package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class MaxAfterMaxHourFeeStrategyTest extends TimePeriodFeeStrategyTest {

    @Override
    TimePeriodFeeStrategy.Supplier supplier() {
        return MaxAfterMaxHourFeeStrategy.partial(Duration.ofHours(24), Jpy.of(1_000));
    }

    @Test
    @DisplayName("最大料金")
    void calcFee_max() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 10:00 ~ 07:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 10, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 7, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(1_000));
    }


    @Test
    @DisplayName("最大料金 (24時間越え)")
    void calcFee_over_maxHour() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 10:00 ~ (+1 day) 11:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 10, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 11, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(1_300));
    }

    @Test
    @DisplayName("48時間料金")
    void calcFee_48hours() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 10:00 ~ (+2 day) 09:59
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 10, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 3, 9, 59);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(2_000));
    }
}