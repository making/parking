package am.ik.lab.parking.domain.parking;

/**
 * 駐車場
 */
public class ParkingLot {

    private final ParkingLotId id;

    private final NumberOfParkingSpace capacity;

    public ParkingLot(ParkingLotId id, NumberOfParkingSpace capacity) {
        this.id = id;
        this.capacity = capacity;
    }
}
