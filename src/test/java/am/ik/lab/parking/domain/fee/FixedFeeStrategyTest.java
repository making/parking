package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FixedFeeStrategyTest {

    @DisplayName("単位時間ぴったり")
    @Test
    void calcFee_just() {
        final FixedFeeStrategy strategy = new FixedFeeStrategy(BaseFee.of(Duration.ofMinutes(30), Jpy.of(100)));
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 8, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 9, 0);
        assertThat(strategy.calcFee(in, out)).isEqualTo(Jpy.of(200));
    }

    @DisplayName("5分オーバー")
    @Test
    void calcFee_over() {
        final FixedFeeStrategy strategy = new FixedFeeStrategy(BaseFee.of(Duration.ofMinutes(30), Jpy.of(100)));
        final LocalDateTime in = LocalDateTime.of(2019, 4, 1, 8, 0);
        final LocalDateTime out = LocalDateTime.of(2019, 4, 1, 9, 5);
        assertThat(strategy.calcFee(in, out)).isEqualTo(Jpy.of(300));
    }
}