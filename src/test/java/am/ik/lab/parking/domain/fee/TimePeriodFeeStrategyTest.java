package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

abstract class TimePeriodFeeStrategyTest {

    abstract TimePeriodFeeStrategy.Supplier supplier();

    @Test
    @DisplayName("昼料金")
    public void calcFee_daytime() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 08:00 ~ 11:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 8, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 11, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(900));
    }

    @Test
    @DisplayName("夜料金 (0:00過ぎ)")
    public void calcFee_night_after_midnight() {

        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 00:00 ~ 03:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 0, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 3, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(300));
    }

    @Test
    @DisplayName("夜料金 (0:00前)")
    public void calcFee_night_before_midnight() {
        // 08:00 ~ 22:00  20 min => JPY 100
        // 22:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(22, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 22:00 ~ 23:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 22, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 23, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(100));
    }

    @Test
    @DisplayName("夜料金 (0:00またぎ)")
    public void calcFee_night_before_midnight_overnight() {
        // 08:00 ~ 22:00  20 min => JPY 100
        // 22:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(22, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 22:00 ~ 01:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 22, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 1, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(300));
    }


    @Test
    @DisplayName("昼料金始まり、夜料金終わり")
    public void calcFee_in_daytime_out_night() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 23:00 ~ 02:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 23, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 2, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(500));
    }

    @Test
    @DisplayName("夜料金始まり、昼料金終わり")
    public void calcFee_in_night_out_daytime() {
        // 08:00 ~ 00:00  20 min => JPY 100
        // 00:00 ~ 08:00  60 min => JPY 100
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(0, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)));

        // 06:00 ~ 09:00
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 6, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 9, 0);
        final MonetaryAmount fee = strategy.calcFee(in, out);
        assertThat(fee).isEqualTo(Jpy.of(500));
    }

    @Test
    @DisplayName("昼料金エッジケース")
    public void calcFee_daytime_edge() {
        // 08:00 ~ 19:00  15 min => JPY 330
        // 19:00 ~ 08:00  60 min => JPY 110
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(19, 0),
            BaseFee.of(Duration.ofMinutes(15), Jpy.of(330)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(110)));

        {
            // 18:59 ~ 19:14
            final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 18, 59);
            final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 19, 14);
            final MonetaryAmount fee = strategy.calcFee(in, out);
            assertThat(fee).isEqualTo(Jpy.of(330));
        }
        {
            // 18:59 ~ 19:15
            final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 18, 59);
            final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 19, 15);
            final MonetaryAmount fee = strategy.calcFee(in, out);
            assertThat(fee).isEqualTo(Jpy.of(440));
        }
    }

    @Test
    @DisplayName("昼料金エッジケース2")
    public void calcFee_daytime_edge2() {
        // 08:00 ~ 19:00  15 min => JPY 330
        // 19:00 ~ 08:00  60 min => JPY 110
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(19, 0),
            BaseFee.of(Duration.ofMinutes(15), Jpy.of(330)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(110)));

        {
            // 18:30 ~ 19:00
            final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 18, 30);
            final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 19, 0);
            final MonetaryAmount fee = strategy.calcFee(in, out);
            assertThat(fee).isEqualTo(Jpy.of(660));
        }
        {
            // 18:30 ~ 19:01
            final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 18, 30);
            final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 19, 1);
            final MonetaryAmount fee = strategy.calcFee(in, out);
            assertThat(fee).isEqualTo(Jpy.of(770));
        }
    }

    @Test
    @DisplayName("夜料金エッジケース")
    public void calcFee_night_edge() {
        // 08:00 ~ 19:00  15 min => JPY 330
        // 19:00 ~ 08:00  60 min => JPY 110
        final FeeStrategy strategy = supplier().supply(
            LocalTime.of(8, 0),
            LocalTime.of(19, 0),
            BaseFee.of(Duration.ofMinutes(15), Jpy.of(330)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(110)));

        {
            // 07:59 ~ 08:59
            final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 7, 59);
            final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 8, 59);
            final MonetaryAmount fee = strategy.calcFee(in, out);
            assertThat(fee).isEqualTo(Jpy.of(110));
        }
        {
            // 07:59 ~ 09:00
            final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 7, 59);
            final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 9, 0);
            final MonetaryAmount fee = strategy.calcFee(in, out);
            assertThat(fee).isEqualTo(Jpy.of(440));
        }
    }
}