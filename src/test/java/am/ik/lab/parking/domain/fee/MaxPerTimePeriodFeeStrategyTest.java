package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class MaxPerTimePeriodFeeStrategyTest extends TimePeriodFeeStrategyTest {

    @Override
    TimePeriodFeeStrategy.Supplier supplier() {
        return MaxPerTimePeriodFeeStrategy.partial(Jpy.of(1_000), Jpy.of(500));
    }

    @DisplayName("昼時間の最大料金")
    @Test
    void calcFee_over_max_in_daytime() {
        final MaxPerTimePeriodFeeStrategy strategy = new MaxPerTimePeriodFeeStrategy(
            LocalTime.of(8, 0),
            LocalTime.of(22, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)),
            Jpy.of(1_000),
            null /* no limit */);

        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 10, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 7, 0);
        assertThat(strategy.calcFee(in, out)).isEqualTo(Jpy.of(1_900));
    }

    @DisplayName("昼時間と夜時間の最大料金")
    @Test
    void calcFee_over_max_in_daytime_and_night() {
        final MaxPerTimePeriodFeeStrategy strategy = new MaxPerTimePeriodFeeStrategy(
            LocalTime.of(8, 0),
            LocalTime.of(22, 0),
            BaseFee.of(Duration.ofMinutes(20), Jpy.of(100)),
            BaseFee.of(Duration.ofMinutes(60), Jpy.of(100)),
            Jpy.of(1_000),
            Jpy.of(500));

        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 10, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 2, 7, 0);
        assertThat(strategy.calcFee(in, out)).isEqualTo(Jpy.of(1_500));
    }
}