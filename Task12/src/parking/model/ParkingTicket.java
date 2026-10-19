package parking.model;

import parking.rule.Pass;
import java.time.LocalTime;

public class ParkingTicket {
    private final long ticketId;
    private final Vehicle vehicle;
    private final Pass pass;
    private final int floor;
    private final int slot;
    private final long entryTimeMinutes;
    private final LocalTime entryClockTime;
    private boolean active;

    public ParkingTicket(long ticketId, Vehicle vehicle, Pass pass,
                         int floor, int slot, long entryTimeMinutes, LocalTime entryClockTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.pass = pass;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMinutes = entryTimeMinutes;
        this.entryClockTime = entryClockTime;
        this.active = true;
    }

    public long getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public Pass getPass() { return pass; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMinutes() { return entryTimeMinutes; }
    public LocalTime getEntryClockTime() { return entryClockTime; }
    public boolean isActive() { return active; }

    public void markCompleted() { this.active = false; }

    @Override
    public String toString() {
        String passInfo = (pass != null) ? pass.getPassType() : "Standard";
        return String.format("Ticket#%d [%s, %s, Pos=(%d,%d), Entry=%s, Active=%b]",
                ticketId, vehicle.getRegNo(), passInfo, floor, slot, entryClockTime, active);
    }
}
