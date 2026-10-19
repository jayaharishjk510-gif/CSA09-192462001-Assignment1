package parking.service;

import parking.model.ElectricCar;
import parking.model.ParkingSlot;
import parking.model.ParkingTicket;
import parking.model.Vehicle;

/**
 * Service orchestrator in parking.service package.
 * Coordinates parking slots, ticket sequencing, and release workflows.
 */
public class ParkingFacility {
    private static long sequenceCounter = 5000;

    // Encapsulated 2D Jagged array of slot entities
    private final ParkingSlot[][] slots;

    public ParkingFacility(int[] slotsPerFloor, int evSlotsPerFloor) {
        slots = new ParkingSlot[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                boolean isEVBay = (s < evSlotsPerFloor); // First 'evSlotsPerFloor' have chargers
                slots[f][s] = new ParkingSlot(f, s, isEVBay);
            }
        }
    }

    /**
     * Allocates a slot suitable for the vehicle type.
     * EVs are preferentially allocated to EV bays.
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle, long currentMinutes) {
        if (vehicle == null) throw new IllegalArgumentException("Cannot park a null vehicle.");

        boolean needsEV = (vehicle instanceof ElectricCar);

        // Pass 1: If EV, look for EV-compatible free bay
        if (needsEV) {
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    ParkingSlot slot = slots[f][s];
                    if (!slot.isOccupied() && slot.hasChargingPort()) {
                        slot.assignVehicle(vehicle);
                        return new ParkingTicket(++sequenceCounter, vehicle, f, s, currentMinutes);
                    }
                }
            }
        }

        // Pass 2: Look for regular bay
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                ParkingSlot slot = slots[f][s];
                if (!slot.isOccupied() && (!slot.hasChargingPort() || !needsEV)) {
                    slot.assignVehicle(vehicle);
                    return new ParkingTicket(++sequenceCounter, vehicle, f, s, currentMinutes);
                }
            }
        }

        return null; // Full
    }

    public synchronized Vehicle vacateSlot(int floor, int slot) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            throw new IndexOutOfBoundsException("Invalid slot coordinates (" + floor + "," + slot + ")");
        }
        return slots[floor][slot].vacate();
    }

    public ParkingSlot getSlot(int floor, int slot) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            throw new IndexOutOfBoundsException("Invalid slot coordinates");
        }
        return slots[floor][slot];
    }
}
