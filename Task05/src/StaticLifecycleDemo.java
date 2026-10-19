/**
 * Task 5: Apply static Members and the Object Life Cycle
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Demonstrates:
 * 1. Static vs Instance variables (Global Ticket Counter & Campus Occupancy).
 * 2. Static methods & Static Initializer Blocks.
 * 3. Multi-facility Gates (North Gate vs South Gate) Collision Analysis.
 * 4. Pass-by-Value mechanism in Java (Reference Value Passing).
 * 5. Object Life Cycle & Garbage Collection Eligibility.
 */
public class StaticLifecycleDemo {

    // Domain Model with Static Members
    static class CampusTicket {
        // Static counter: Belongs to the class, shared across all instances
        private static long globalSequenceCounter = 1000;
        private static int activeVehiclesOnCampus = 0;

        // Static Initializer Block
        static {
            System.out.println("[Static Block] CampusTicket class loaded into JVM Metaspace. Initialized counter to "
                    + globalSequenceCounter);
        }

        private final long ticketId;
        private final String regNo;
        private boolean active;

        public CampusTicket(String regNo) {
            this.ticketId = ++globalSequenceCounter; // Atomic monotonic increment
            this.regNo = regNo;
            this.active = true;
            activeVehiclesOnCampus++;
        }

        public void checkout() {
            if (this.active) {
                this.active = false;
                activeVehiclesOnCampus--;
            }
        }

        public long getTicketId() { return ticketId; }
        public String getRegNo() { return regNo; }
        public boolean isActive() { return active; }

        public static int getActiveCampusOccupancy() {
            return activeVehiclesOnCampus;
        }

        public static long getLastIssuedId() {
            return globalSequenceCounter;
        }
    }

    // Gate Facility Class for Multi-Gate Analysis
    static class FacilityGate {
        private final String gateName;
        // Case A: Instance counter (Dangerous without facility prefix)
        private long instanceCounter = 0;

        public FacilityGate(String gateName) {
            this.gateName = gateName;
        }

        public String issueTicketInstanceCounter(String regNo) {
            instanceCounter++;
            return "TKT-" + instanceCounter; // Potential collision across gates!
        }

        public String issueTicketPrefixed(String regNo) {
            instanceCounter++;
            return gateName + "-" + instanceCounter; // Collision-free namespaced
        }
    }

    // Pass-by-value demonstration method
    public static void modifyTicket(CampusTicket ticket, String newReg) {
        // Reassignment affects only the local parameter reference
        ticket = new CampusTicket(newReg);
    }

    public static void closeTicketInMethod(CampusTicket ticket) {
        // Mutating fields on the referenced heap object affects caller's object
        ticket.checkout();
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 5: STATIC MEMBERS, MULTI-GATE ANALYSIS & OBJECT LIFECYCLE");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        // 1. Static Counter & Occupancy
        System.out.println("--- 1. Static Ticket Sequence & Global Occupancy ---");
        CampusTicket t1 = new CampusTicket("TN01AA1111");
        CampusTicket t2 = new CampusTicket("TN02BB2222");
        System.out.printf("Ticket 1 ID: %d | Ticket 2 ID: %d%n", t1.getTicketId(), t2.getTicketId());
        System.out.printf("Campus Active Occupancy: %d vehicles%n", CampusTicket.getActiveCampusOccupancy());

        t1.checkout();
        System.out.println("After Ticket 1 checkout:");
        System.out.printf("Campus Active Occupancy: %d vehicles%n", CampusTicket.getActiveCampusOccupancy());

        // 2. Multi-Gate Scenario: North Gate vs South Gate
        System.out.println("\n--- 2. Multi-Gate Facility Analysis (North Gate vs South Gate) ---");
        FacilityGate northGate = new FacilityGate("NORTH");
        FacilityGate southGate = new FacilityGate("SOUTH");

        String nTkt1 = northGate.issueTicketInstanceCounter("TN03CC3333");
        String sTkt1 = southGate.issueTicketInstanceCounter("TN04DD4444");
        System.out.println("Instance Counter without prefix:");
        System.out.println("  North Gate Ticket: " + nTkt1);
        System.out.println("  South Gate Ticket: " + sTkt1);
        System.out.println("  COLLISION DETECTED! Both gates issued 'TKT-1' for different cars!");

        String nPrefixed = northGate.issueTicketPrefixed("TN03CC3333");
        String sPrefixed = southGate.issueTicketPrefixed("TN04DD4444");
        System.out.println("Instance Counter with Gate Prefix:");
        System.out.println("  North Gate: " + nPrefixed + " | South Gate: " + sPrefixed + " (Conflict Resolved)");

        // 3. Pass-by-Value Verification
        System.out.println("\n--- 3. Java Pass-by-Value Mechanism ---");
        CampusTicket testTicket = new CampusTicket("TN05EE5555");
        System.out.println("Before modifyTicket(): Ticket ID = " + testTicket.getTicketId() + ", Reg = " + testTicket.getRegNo());
        modifyTicket(testTicket, "TN99ZZ9999");
        System.out.println("After modifyTicket() [Reassignment]: Reg is STILL = " + testTicket.getRegNo() + " (Caller reference unchanged)");

        closeTicketInMethod(testTicket);
        System.out.println("After closeTicketInMethod() [Mutation]: isActive = " + testTicket.isActive() + " (Heap object mutated)");

        // 4. Object Lifecycle & GC Eligibility
        System.out.println("\n--- 4. Object Life Cycle & Garbage Collection ---");
        System.out.println("Step A: Object created on Heap via 'new' -> Strong Reference in stack");
        System.out.println("Step B: Vehicle checks out, ticket closed");
        System.out.println("Step C: Local reference nullified: `testTicket = null;`");
        testTicket = null;
        System.out.println("Step D: Object has 0 references. It is now an unreachable island on Heap.");
        System.out.println("Step E: Eligible for Garbage Collection (Young Gen Eden / Survivor / Tenured Old Gen).");
        System.out.println("========================================================================");
    }
}
