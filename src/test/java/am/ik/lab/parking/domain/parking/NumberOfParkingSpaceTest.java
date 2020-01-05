package am.ik.lab.parking.domain.parking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberOfParkingSpaceTest {

    @Test
    void isOverCapacity() {
        final NumberOfParkingSpace numberOfParkingSpace = new NumberOfParkingSpace(10);
        assertThat(numberOfParkingSpace.isOverCapacity(9)).isFalse();
        assertThat(numberOfParkingSpace.isOverCapacity(10)).isFalse();
        assertThat(numberOfParkingSpace.isOverCapacity(11)).isTrue();
    }
}