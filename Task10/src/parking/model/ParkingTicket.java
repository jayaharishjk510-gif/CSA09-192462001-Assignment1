package parking.model;

public class ParkingTicket {
    private final Long ticketId; // Wrapper class demonstrating autoboxing
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final long entryTimeMinutes;
    private boolean active;

    public ParkingTicket(Long ticketId, Vehicle vehicle, int floor, int slot, long entryTimeMinutes) {
        if (ticketId == null || ticketId <= 0) {
            throw new IllegalArgumentException("Ticket ID must be a positive non-null value.");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMinutes = entryTimeMinutes;
        this.active = true;
    }

    public Long getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMinutes() { return entryTimeMinutes; }
    public boolean isActive() { return active; }

    public void markCompleted() {
        this.active = false;
    }

    @Override
    public String toString() {
        return String.format("Ticket#%d [%s at Floor %d, Slot %d, Active=%b]",
                ticketId, vehicle.getRegNo(), floor, slot, active);
    }
}
