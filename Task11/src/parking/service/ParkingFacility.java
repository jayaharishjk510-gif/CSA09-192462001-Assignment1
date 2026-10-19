package parking.service;

import parking.model.*;

import java.util.*;

/**
 * Robust ParkingFacility service with multi-structure indexing.
 * Supports Array, ArrayList, and HashMap for comparative exit lookups.
 */
public class ParkingFacility {
    private static long ticketSequence = 1000;

    private final ParkingSlot[][] slots;
    private final int totalCapacity;
    private int currentOccupancy;

    // Storing active tickets across 3 structures for complexity comparison:
    private final ParkingTicket[] ticketArray; // Fixed array approach
    private final List<ParkingTicket> ticketList; // ArrayList approach
    private final Map<String, ParkingTicket> ticketMap; // HashMap keyed by regNo

    public ParkingFacility(int[] slotsPerFloor, int evSlotsPerFloor) {
        slots = new ParkingSlot[slotsPerFloor.length][];
        int sum = 0;
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                boolean isEV = (s < evSlotsPerFloor);
                slots[f][s] = new ParkingSlot(f, s, isEV);
                sum++;
            }
        }
        this.totalCapacity = sum;
        this.currentOccupancy = 0;
        this.ticketArray = new ParkingTicket[totalCapacity];
        this.ticketList = new ArrayList<>(totalCapacity);
        this.ticketMap = new HashMap<>(totalCapacity);
    }

    public synchronized ParkingTicket parkVehicle(Vehicle vehicle, UserAffiliation affiliation, long entryMinutes) {
        if (vehicle == null) throw new IllegalArgumentException("Cannot park null vehicle.");

        // Invalid Case Check: Duplicate registration number detection
        if (ticketMap.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Duplicate entry rejected: Vehicle "
                    + vehicle.getRegNo() + " is already inside the facility!");
        }

        // Boundary Check: Facility full
        if (currentOccupancy >= totalCapacity) {
            return null; // No free slots
        }

        boolean needsEV = (vehicle instanceof ElectricCar);

        // Pass 1: If EV, search for dedicated EV charging bay
        if (needsEV) {
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    ParkingSlot slot = slots[f][s];
                    if (!slot.isOccupied() && slot.hasChargingPort()) {
                        return assignAndIssue(slot, vehicle, affiliation, entryMinutes);
                    }
                }
            }
        }

        // Pass 2: Search regular bay (EV can take regular bay only if no chargers left)
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                ParkingSlot slot = slots[f][s];
                if (!slot.isOccupied() && !slot.hasChargingPort()) {
                    return assignAndIssue(slot, vehicle, affiliation, entryMinutes);
                }
            }
        }

        // Pass 3: Fallback (any free bay)
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                ParkingSlot slot = slots[f][s];
                if (!slot.isOccupied()) {
                    return assignAndIssue(slot, vehicle, affiliation, entryMinutes);
                }
            }
        }

        return null;
    }

    private ParkingTicket assignAndIssue(ParkingSlot slot, Vehicle vehicle, UserAffiliation affiliation, long entryMinutes) {
        slot.assignVehicle(vehicle);
        long tId = ++ticketSequence;
        ParkingTicket ticket = new ParkingTicket(tId, vehicle, affiliation, slot.getFloor(), slot.getSlotNumber(), entryMinutes);

        // Index in Array
        ticketArray[currentOccupancy] = ticket;
        // Index in List
        ticketList.add(ticket);
        // Index in Map
        ticketMap.put(vehicle.getRegNo(), ticket);

        currentOccupancy++;
        return ticket;
    }

    // Lookup 1: Linear Scan in Array (O(n))
    public ParkingTicket findTicketByArray(String regNo) {
        for (int i = 0; i < currentOccupancy; i++) {
            if (ticketArray[i] != null && ticketArray[i].getVehicle().getRegNo().equals(regNo)) {
                return ticketArray[i];
            }
        }
        return null;
    }

    // Lookup 2: Linear Scan in ArrayList (O(n))
    public ParkingTicket findTicketByList(String regNo) {
        for (ParkingTicket t : ticketList) {
            if (t.getVehicle().getRegNo().equals(regNo)) {
                return t;
            }
        }
        return null;
    }

    // Lookup 3: Hash Table Lookup (O(1))
    public ParkingTicket findTicketByMap(String regNo) {
        return ticketMap.get(regNo);
    }

    public synchronized Vehicle vacateVehicle(String regNo) {
        ParkingTicket ticket = ticketMap.remove(regNo);
        if (ticket == null) {
            throw new NoSuchElementException("Vehicle " + regNo + " not found in active parking roster.");
        }

        ticket.markCompleted();
        ticketList.remove(ticket);

        // Remove from array (shift left or replace with last element)
        for (int i = 0; i < currentOccupancy; i++) {
            if (ticketArray[i] == ticket) {
                ticketArray[i] = ticketArray[currentOccupancy - 1];
                ticketArray[currentOccupancy - 1] = null;
                break;
            }
        }

        ParkingSlot slot = slots[ticket.getFloor()][ticket.getSlot()];
        Vehicle vacated = slot.vacate();
        currentOccupancy--;
        return vacated;
    }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public int getTotalCapacity() { return totalCapacity; }
    public int getFreeSlotsCount() { return totalCapacity - currentOccupancy; }
}
