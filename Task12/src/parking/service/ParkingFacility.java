package parking.service;

import parking.model.ParkingSlot;
import parking.model.ParkingTicket;
import parking.model.Vehicle;
import parking.rule.Pass;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Extended 5-Floor Parking Facility with Variable Slots per Floor.
 * Uses a Jagged Array to natively support non-uniform architectural layouts.
 */
public class ParkingFacility {
    private static long ticketSequence = 7000;

    // Jagged 2D array: floors have different slot capacities
    private final ParkingSlot[][] slots;
    private final int[] slotsPerFloor;
    private final int totalCapacity;
    private int currentOccupancy;
    private final Map<String, ParkingTicket> activeTickets;

    public ParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length != 5) {
            throw new IllegalArgumentException("Facility must be initialized with exactly 5 floor configurations.");
        }
        this.slotsPerFloor = slotsPerFloor.clone();
        this.slots = new ParkingSlot[5][];
        int sum = 0;
        for (int f = 0; f < 5; f++) {
            this.slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                this.slots[f][s] = new ParkingSlot(f, s);
                sum++;
            }
        }
        this.totalCapacity = sum;
        this.currentOccupancy = 0;
        this.activeTickets = new HashMap<>();
    }

    public synchronized ParkingTicket allocate(Vehicle vehicle, Pass pass, long entryMinutes, LocalTime clockTime) {
        if (vehicle == null) throw new IllegalArgumentException("Cannot park null vehicle.");
        if (activeTickets.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Duplicate entry: Vehicle " + vehicle.getRegNo() + " is already parked.");
        }
        if (currentOccupancy >= totalCapacity) {
            return null; // Facility full
        }

        // Jagged array scan: floor by floor, slot by slot respecting each floor's distinct length
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (!slots[f][s].isOccupied()) {
                    slots[f][s].assignVehicle(vehicle);
                    long tId = ++ticketSequence;
                    ParkingTicket ticket = new ParkingTicket(tId, vehicle, pass, f, s, entryMinutes, clockTime);
                    activeTickets.put(vehicle.getRegNo(), ticket);
                    currentOccupancy++;
                    return ticket;
                }
            }
        }
        return null;
    }

    public synchronized Vehicle release(String regNo) {
        ParkingTicket ticket = activeTickets.remove(regNo);
        if (ticket == null) {
            throw new IllegalStateException("Vehicle " + regNo + " not found in active parking roster.");
        }
        ticket.markCompleted();
        ParkingSlot slot = slots[ticket.getFloor()][ticket.getSlot()];
        Vehicle v = slot.vacate();
        currentOccupancy--;
        return v;
    }

    public ParkingTicket getActiveTicket(String regNo) {
        return activeTickets.get(regNo);
    }

    public int getFloorCapacity(int floor) {
        return slots[floor].length;
    }

    public int getTotalCapacity() { return totalCapacity; }
    public int getCurrentOccupancy() { return currentOccupancy; }
    public int getAvailableSlots() { return totalCapacity - currentOccupancy; }
}
