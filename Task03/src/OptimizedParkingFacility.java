import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Task 3 Alternative Concept:
 * O(1) Allocation and Release using Free Slot Stacks and Floor Occupancy Counters.
 * Student: Jaya Harish J (Reg No: 192462001)
 */
public class OptimizedParkingFacility {

    static class SlotPosition {
        final int floor;
        final int slot;
        SlotPosition(int floor, int slot) {
            this.floor = floor;
            this.slot = slot;
        }
        @Override
        public String toString() { return "[" + floor + ", " + slot + "]"; }
    }

    private final String[][] slots;
    private final int[] freeSlotsPerFloor;
    private final Deque<SlotPosition> freeSlotStack;

    public OptimizedParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        freeSlotsPerFloor = new int[slotsPerFloor.length];
        freeSlotStack = new ArrayDeque<>();

        // Initialize slots and push all available positions onto the stack (LIFO order or FIFO order)
        // Pushing from highest floor/slot to lowest so lowest floor pops first:
        for (int f = slotsPerFloor.length - 1; f >= 0; f--) {
            slots[f] = new String[slotsPerFloor[f]];
            freeSlotsPerFloor[f] = slotsPerFloor[f];
            for (int s = slotsPerFloor[f] - 1; s >= 0; s--) {
                freeSlotStack.push(new SlotPosition(f, s));
            }
        }
    }

    /** O(1) Allocation: Pops from free stack immediately without iterating floors */
    public int[] allocate(String vehicleNo) {
        if (freeSlotStack.isEmpty()) {
            return null; // Facility full
        }
        SlotPosition pos = freeSlotStack.pop();
        slots[pos.floor][pos.slot] = vehicleNo;
        freeSlotsPerFloor[pos.floor]--;
        return new int[]{pos.floor, pos.slot};
    }

    /** O(1) Release: Validates and pushes vacated slot directly back onto free stack */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            return false;
        }
        if (slots[floor][slot] == null) {
            return false;
        }
        slots[floor][slot] = null;
        freeSlotsPerFloor[floor]++;
        freeSlotStack.push(new SlotPosition(floor, slot));
        return true;
    }

    public static void main(String[] args) {
        System.out.println("=== O(1) Stack-Based Optimized Parking Facility Execution ===");
        OptimizedParkingFacility p = new OptimizedParkingFacility(new int[]{2, 1});
        System.out.println(Arrays.toString(p.allocate("TN01AB1111")));
        System.out.println(Arrays.toString(p.allocate("TN02CD2222")));
        System.out.println(Arrays.toString(p.allocate("TN03EF3333")));
        System.out.println(Arrays.toString(p.allocate("TN04GH4444")));
        System.out.println(p.release(0, 1) + " " + p.release(0, 1) + " " + p.release(5, 0));
        System.out.println(Arrays.toString(p.allocate("TN04GH4444")));
    }
}
