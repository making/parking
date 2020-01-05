package am.ik.lab.parking.domain.parking;

/**
 * 駐車可能台数
 */
public class NumberOfParkingSpace {

    private final int value;

    public NumberOfParkingSpace(int value) {
        this.value = value;
    }

    public boolean isOverCapacity(int n) {
        return n > this.value;
    }
}
