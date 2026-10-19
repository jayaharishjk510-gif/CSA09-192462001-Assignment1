package parking.model;

public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private final boolean hasChargingPort;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber, boolean hasChargingPort) {
        if (floor < 0 || slotNumber < 0) {
            throw new IllegalArgumentException("Indices must be non-negative.");
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
        if (vehicle == null) throw new IllegalArgumentException("Cannot park null vehicle.");
        if (isOccupied()) throw new IllegalStateException("Slot (" + floor + "," + slotNumber + ") is occupied.");
        this.parkedVehicle = vehicle;
    }

    public Vehicle vacate() {
        if (!isOccupied()) throw new IllegalStateException("Slot (" + floor + "," + slotNumber + ") is already vacant.");
        Vehicle v = this.parkedVehicle;
        this.parkedVehicle = null;
        return v;
    }
}
