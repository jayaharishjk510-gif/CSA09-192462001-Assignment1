/**
 * Task 4: Design Classes, Objects and Constructors
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Demonstrates:
 * 1. Default, Parameterized, Overloaded, and Copy Constructors.
 * 2. Constructor chaining using `this(...)` to eliminate redundancy.
 * 3. Static Factory Methods vs JavaBeans (Getters/Setters) pattern.
 * 4. Object validity guarantees and Stack vs Heap memory layout.
 */
public class ConstructorChainingDemo {

    // Domain Class: ParkingTicket
    public static class ParkingTicket {
        private final long ticketId;
        private final String regNo;
        private final int floor;
        private final int slot;
        private final long entryTimeMinutes;
        private final String userType;
        private boolean isActive;

        // 1. Canonical Parameterized Constructor (Enforces Invariants)
        public ParkingTicket(long ticketId, String regNo, int floor, int slot,
                             long entryTimeMinutes, String userType, boolean isActive) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            if (floor < 0 || slot < 0) {
                throw new IllegalArgumentException("Floor and slot indices must be non-negative.");
            }
            this.ticketId = ticketId;
            this.regNo = regNo.toUpperCase().trim();
            this.floor = floor;
            this.slot = slot;
            this.entryTimeMinutes = entryTimeMinutes;
            this.userType = (userType == null) ? "Visitor" : userType;
            this.isActive = isActive;
        }

        // 2. Overloaded Constructor: Chains to Canonical Constructor using `this(...)`
        public ParkingTicket(long ticketId, String regNo, int floor, int slot) {
            // Chains with default entry time = 0 min, userType = "Visitor", isActive = true
            this(ticketId, regNo, floor, slot, System.currentTimeMillis() / 60000L, "Visitor", true);
        }

        // 3. Overloaded Constructor with User Type: Chains to Canonical Constructor
        public ParkingTicket(long ticketId, String regNo, int floor, int slot, String userType) {
            this(ticketId, regNo, floor, slot, System.currentTimeMillis() / 60000L, userType, true);
        }

        // 4. Copy Constructor: Generates a verified replica (e.g., re-issuing duplicate ticket)
        public ParkingTicket(ParkingTicket other) {
            if (other == null) {
                throw new IllegalArgumentException("Cannot copy a null ticket reference.");
            }
            // Defensively copies all invariant fields
            this.ticketId = other.ticketId;
            this.regNo = other.regNo;
            this.floor = other.floor;
            this.slot = other.slot;
            this.entryTimeMinutes = other.entryTimeMinutes;
            this.userType = other.userType;
            this.isActive = other.isActive;
        }

        // 5. Static Factory Method Alternative
        public static ParkingTicket issueVisitorTicket(long ticketId, String regNo, int floor, int slot) {
            return new ParkingTicket(ticketId, regNo, floor, slot, System.currentTimeMillis() / 60000L, "Visitor", true);
        }

        public long getTicketId() { return ticketId; }
        public String getRegNo() { return regNo; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public long getEntryTimeMinutes() { return entryTimeMinutes; }
        public String getUserType() { return userType; }
        public boolean isActive() { return isActive; }
        public void close() { this.isActive = false; }

        @Override
        public String toString() {
            return String.format("Ticket[ID=%d, Reg=%s, Pos=(%d,%d), Entry=%d min, User=%s, Active=%b]",
                    ticketId, regNo, floor, slot, entryTimeMinutes, userType, isActive);
        }
    }

    // Mutable JavaBeans alternative (for comparison in analysis)
    public static class MutableBeanTicket {
        private long ticketId;
        private String regNo;
        private int floor;
        private int slot;

        public MutableBeanTicket() {} // Default constructor (leaves object in invalid state!)

        public void setTicketId(long ticketId) { this.ticketId = ticketId; }
        public void setRegNo(String regNo) { this.regNo = regNo; }
        public void setFloor(int floor) { this.floor = floor; }
        public void setSlot(int slot) { this.slot = slot; }

        @Override
        public String toString() {
            return "BeanTicket[ID=" + ticketId + ", Reg=" + regNo + ", Pos=(" + floor + "," + slot + ")]";
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 4: CLASSES, CONSTRUCTORS & MEMORY ALLOCATION");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        // 1. Chained Constructor Execution
        ParkingTicket t1 = new ParkingTicket(1001L, "TN01AA1111", 0, 1);
        System.out.println("Chained Constructor Ticket: " + t1);

        // 2. Overloaded Constructor with Affiliation
        ParkingTicket t2 = new ParkingTicket(1002L, "TN02BB2222", 1, 0, "Student");
        System.out.println("Overloaded Constructor Ticket: " + t2);

        // 3. Copy Constructor (Duplicate Ticket Issue)
        ParkingTicket tDuplicate = new ParkingTicket(t1);
        System.out.println("Duplicate Ticket (Copy Constructor): " + tDuplicate);
        System.out.println("Identity Equality (t1 == tDuplicate): " + (t1 == tDuplicate) + " (Distinct Heap Objects)");
        System.out.println("Data Equality (t1.getTicketId() == tDuplicate.getTicketId()): "
                + (t1.getTicketId() == tDuplicate.getTicketId()));

        // 4. Static Factory Method
        ParkingTicket tFactory = ParkingTicket.issueVisitorTicket(1003L, "TN03CC3333", 2, 4);
        System.out.println("Factory Created Ticket: " + tFactory);

        // 5. JavaBeans Comparison: Risk of partial initialization
        MutableBeanTicket bean = new MutableBeanTicket();
        System.out.println("\nJavaBean ticket before setters (Incomplete State): " + bean);
        bean.setTicketId(1004L);
        bean.setRegNo("TN04DD4444");
        bean.setFloor(0);
        bean.setSlot(2);
        System.out.println("JavaBean ticket after setters: " + bean);

        // 6. Memory Performance Benchmark
        int n = 100_000;
        long startChain = System.nanoTime();
        for (int i = 0; i < n; i++) {
            ParkingTicket dummy = new ParkingTicket(i, "TN01XX", 0, 0);
        }
        long durationChain = System.nanoTime() - startChain;

        long startBean = System.nanoTime();
        for (int i = 0; i < n; i++) {
            MutableBeanTicket b = new MutableBeanTicket();
            b.setTicketId(i);
            b.setRegNo("TN01XX");
            b.setFloor(0);
            b.setSlot(0);
        }
        long durationBean = System.nanoTime() - startBean;

        System.out.printf("\nCreation Time for %d tickets:%n", n);
        System.out.printf("  Constructor Chaining (Immutable): %.2f ms%n", durationChain / 1e6);
        System.out.printf("  JavaBean Setters (Mutable):       %.2f ms%n", durationBean / 1e6);
        System.out.println("========================================================================");
    }
}
