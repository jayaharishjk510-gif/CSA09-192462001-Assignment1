package parking.app;

import parking.model.*;
import parking.service.FeeCalculator;
import parking.service.ParkingFacility;

/**
 * Task 10: Enforce Encapsulation through Packages and Access Modifiers
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Demonstrates:
 * 1. Package segregation: parking.model, parking.service, and parking.app.
 * 2. Access control: private fields, protected hooks, public service APIs.
 * 3. Validation guards in constructors and methods.
 * 4. Wrapper classes, Autoboxing pitfalls (Integer Cache == trap, null unboxing).
 * 5. Public mutable fields vs Private validated encapsulation analysis.
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 10: PACKAGES, ACCESS CONTROL & ENCAPSULATION");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        // 1. Package Architecture and Operational Flow
        System.out.println("--- 1. Package Modularity Flow ---");
        // Floor 0: 3 slots (1 EV), Floor 1: 2 slots (0 EV)
        ParkingFacility facility = new ParkingFacility(new int[]{3, 2}, 1);

        Vehicle bike = new TwoWheeler("TN01AA1111");
        Vehicle car = new Car("TN02BB2222");
        Vehicle ev = new ElectricCar("TN03CC3333", 60);

        long entryTime = 1000;
        ParkingTicket tkt1 = facility.parkVehicle(bike, entryTime);
        ParkingTicket tkt2 = facility.parkVehicle(car, entryTime);
        ParkingTicket tkt3 = facility.parkVehicle(ev, entryTime);

        System.out.println("Issued Tickets via Encapsulated Service:");
        System.out.println("  " + tkt1);
        System.out.println("  " + tkt2);
        System.out.println("  " + tkt3);

        // Billing calculation via service
        long exitTime = 1130; // 130 minutes later (3 billable hours)
        FeeCalculator.BillSummary billVisitor = FeeCalculator.generateBill(tkt2, exitTime, 0, false);
        FeeCalculator.BillSummary billStudent = FeeCalculator.generateBill(tkt1, exitTime, 0, true);
        FeeCalculator.BillSummary billEV = FeeCalculator.generateBill(tkt3, exitTime, 12, true); // EV pass holder with 12 kWh charging

        System.out.println("\nGenerated Invoices:");
        System.out.println("  " + billVisitor);
        System.out.println("  " + billStudent);
        System.out.println("  " + billEV);

        // 2. Data Validation Guard Demonstration
        System.out.println("\n--- 2. Data Validation Guard Rejections ---");
        try {
            System.out.println("Attempting to calculate fee with negative duration (-15 mins):");
            car.calculateFee(-15);
        } catch (IllegalArgumentException ex) {
            System.out.println("  [VALIDATION CAUGHT] " + ex.getMessage());
        }

        try {
            System.out.println("Attempting to instantiate vehicle with empty registration plate:");
            new Car("   ");
        } catch (IllegalArgumentException ex) {
            System.out.println("  [VALIDATION CAUGHT] " + ex.getMessage());
        }

        // 3. Wrapper Classes and Autoboxing Pitfalls
        System.out.println("\n--- 3. Autoboxing & Wrapper Class Hazards ---");

        // Hazard A: The Integer Cache Identity Pitfall (== vs .equals)
        Integer cached1 = 100;
        Integer cached2 = 100;
        System.out.println("Integer cached1 = 100, cached2 = 100:");
        System.out.println("  cached1 == cached2: " + (cached1 == cached2) + " (Cached in -128..127 range)");

        Integer uncached1 = 1000;
        Integer uncached2 = 1000;
        System.out.println("Integer uncached1 = 1000, uncached2 = 1000:");
        System.out.println("  uncached1 == uncached2: " + (uncached1 == uncached2) + " [DANGER! Distinct Heap Objects!]");
        System.out.println("  uncached1.equals(uncached2): " + uncached1.equals(uncached2) + " [CORRECT: Value Equality]");

        // Hazard B: NullPointerException during Unboxing
        Long boxedTicketId = null;
        try {
            System.out.println("Attempting unboxing of null wrapper Long to primitive long:");
            long primitiveId = boxedTicketId; // Triggers NullPointerException!
        } catch (NullPointerException npe) {
            System.out.println("  [UNBOXING NPE CAUGHT] Attempted primitive conversion on null wrapper!");
        }

        System.out.println("\n========================================================================");
        System.out.println("Encapsulation prevents corrupting states from caller code.");
        System.out.println("========================================================================");
    }
}
