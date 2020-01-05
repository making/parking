package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class MaxPerDayFeeStrategyTest extends TimePeriodFeeStrategyTest {

    @Override
    TimePeriodFeeStrategy.Supplier supplier() {
        return MaxPerDayFeeStrategy.partial(Jpy.of(1_000));
    }

    @Test
    @DisplayName("最大料金 (0:00過ぎ)")
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
        assertThat(fee).isEqualTo(Jpy.of(1_700));
    }


    @Test
    @DisplayName("最大料金 + 正規料金")
    void calcFee_max_and_regular() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 10:00 ~ (+1 day) 08:10
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 10, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 8, 10);
        final MonetaryAmount fee = strategy.calcFee(in, out);


        // 10:00 ~ 00:00 => 1_000
        // 00:00 ~ 08:00 => 100 * 8 = 800
        // 08:00 ~ 08:10 => 100 * 1 = 100
        //
        // 1_000 + max(1_000, 800 + 100) = 1_900
        assertThat(fee).isEqualTo(Jpy.of(1_900));
    }

    @Test
    @DisplayName("最大料金 * 2")
    void calcFee_over_max_twice() {
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

        // 10:00 ~ 00:00 => 1_000
        // 00:00 ~ 08:00 => 100 * 8 = 800
        // 08:00 ~ 11:00 => 100 * 6 = 600
        //
        // 1_000 + max(1_000, 800 + 600) = 2_000
        assertThat(fee).isEqualTo(Jpy.of(2_000));
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

        // 10:00 ~ 00:00 => 1_000
        // 00:00 ~ 00:00 => 1_000
        // 00:00 ~ 08:00 => 100 * 8 = 800
        // 08:00 ~ 09:59 => 100 * 4 = 400
        //
        // 1_000 + 1_000 + max(1_000, 800 + 400) = 3_000
        assertThat(fee).isEqualTo(Jpy.of(3_000));
    }
}