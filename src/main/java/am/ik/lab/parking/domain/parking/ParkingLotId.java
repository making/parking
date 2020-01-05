package am.ik.lab.parking.domain.parking;

public class ParkingLotId {

    private final int value;

    public ParkingLotId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
