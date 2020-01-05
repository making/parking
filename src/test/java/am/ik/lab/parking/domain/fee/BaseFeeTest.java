package am.ik.lab.parking.domain.fee;

import am.ik.lab.parking.currency.Jpy;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BaseFeeTest {

    @Test
    void fee() {
        final BaseFee baseFee = BaseFee.of(Duration.ofMinutes(20), Jpy.of(100));

        assertThat(baseFee.fee(Duration.ofMinutes(0))).isEqualTo(Jpy.of(0));
        assertThat(baseFee.fee(Duration.ofMinutes(10))).isEqualTo(Jpy.of(100));
        assertThat(baseFee.fee(Duration.ofMinutes(20))).isEqualTo(Jpy.of(100));
        assertThat(baseFee.fee(Duration.ofMinutes(21))).isEqualTo(Jpy.of(200));
        assertThat(baseFee.fee(Duration.ofMinutes(40))).isEqualTo(Jpy.of(200));
        assertThat(baseFee.fee(Duration.ofMinutes(41))).isEqualTo(Jpy.of(300));
    }
}