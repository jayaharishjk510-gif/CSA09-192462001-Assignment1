package parking.model;

public class ParkingTicket {
    private final long ticketId;
    private final Vehicle vehicle;
    private final UserAffiliation affiliation;
    private final int floor;
    private final int slot;
    private final long entryTimeMinutes;
    private boolean active;

    public ParkingTicket(long ticketId, Vehicle vehicle, UserAffiliation affiliation,
                         int floor, int slot, long entryTimeMinutes) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.affiliation = affiliation;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMinutes = entryTimeMinutes;
        this.active = true;
    }

    public long getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public UserAffiliation getAffiliation() { return affiliation; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMinutes() { return entryTimeMinutes; }
    public boolean isActive() { return active; }

    public void markCompleted() { this.active = false; }

    @Override
    public String toString() {
        return String.format("Ticket#%d [%s, %s, Pos=(%d,%d), Entry=%d min, Active=%b]",
                ticketId, vehicle.getRegNo(), affiliation, floor, slot, entryTimeMinutes, active);
    }
}
