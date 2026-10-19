package parking.model;

/**
 * Encapsulated ParkingSlot.
 * Fields are private; mutations enforce state invariants.
 */
public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private final boolean hasChargingPort;
    private Vehicle parkedVehicle; // null if free

    public ParkingSlot(int floor, int slotNumber, boolean hasChargingPort) {
        if (floor < 0 || slotNumber < 0) {
            throw new IllegalArgumentException("Floor and slot numbers must be non-negative.");
        }
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.hasChargingPort = hasChargingPort;
        this.parkedVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean hasChargingPort() { return hasChargingPort; }
    public boolean isOccupied() { return parkedVehicle != null; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void assignVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Cannot park a null vehicle.");
        }
        if (isOccupied()) {
            throw new IllegalStateException("Slot (" + floor + "," + slotNumber + ") is already occupied by " + parkedVehicle);
        }
        this.parkedVehicle = vehicle;
    }

    public Vehicle vacate() {
        if (!isOccupied()) {
            throw new IllegalStateException("Slot (" + floor + "," + slotNumber + ") is already vacant.");
        }
        Vehicle v = this.parkedVehicle;
        this.parkedVehicle = null;
        return v;
    }

    @Override
    public String toString() {
        return String.format("Slot[%d,%d, EV=%b, Occupied=%b]", floor, slotNumber, hasChargingPort, isOccupied());
    }
}
