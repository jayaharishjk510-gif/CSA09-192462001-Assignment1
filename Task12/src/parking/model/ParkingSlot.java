package parking.model;

public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.parkedVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isOccupied() { return parkedVehicle != null; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void assignVehicle(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Cannot park null vehicle.");
        if (isOccupied()) throw new IllegalStateException("Slot (" + floor + "," + slotNumber + ") is occupied.");
        this.parkedVehicle = vehicle;
    }

    public Vehicle vacate() {
        if (!isOccupied()) throw new IllegalStateException("Slot is vacant.");
        Vehicle v = this.parkedVehicle;
        this.parkedVehicle = null;
        return v;
    }

    @Override
    public String toString() {
        return String.format("Slot[%d,%d, Occ=%b]", floor, slotNumber, isOccupied());
    }
}
