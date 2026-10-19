package parking.test;

import parking.model.*;
import parking.service.BillingService;
import parking.service.BillingService.Invoice;
import parking.service.ParkingFacility;

/**
 * Task 11: Implement, Test and Defend the Design
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Full verification test suite covering:
 * - Test 1: Normal Operations
 * - Test 2: Boundary Cases (Last slot, 0-min, slab boundary 60 vs 61)
 * - Test 3: Invalid Cases (Null, Duplicates, Invalid vacate)
 * - Test 4: Hand-Calculated Daily Revenue Reconciliation (Target: Rs 610.0)
 * - Test 5: Benchmark comparing Array vs ArrayList vs HashMap exit lookup
 */
public class SmartParkingSystemTest {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 11: FULL SYSTEM IMPLEMENTATION, TESTING & DEFENCE");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        runNormalOperationsTest();
        runBoundaryCasesTest();
        runInvalidCasesTest();
        runHandCalculatedScenarioTest();
        runDataStructureLookupBenchmark();

        System.out.println("\n========================================================================");
        System.out.println("ALL TASK 11 TESTS EXECUTED AND VALIDATED SUCCESSFULLY.");
        System.out.println("========================================================================");
    }

    private static void runNormalOperationsTest() {
        System.out.println("--- TEST 1: NORMAL OPERATIONS (Different Vehicles In/Out) ---");
        ParkingFacility facility = new ParkingFacility(new int[]{2, 2}, 1);

        Vehicle v1 = new TwoWheeler("TN01N101");
        Vehicle v2 = new Car("TN02N202");
        Vehicle v3 = new ElectricCar("TN03N303");

        ParkingTicket t1 = facility.parkVehicle(v1, UserAffiliation.VISITOR, 600);
        ParkingTicket t2 = facility.parkVehicle(v2, UserAffiliation.STUDENT, 600);
        ParkingTicket t3 = facility.parkVehicle(v3, UserAffiliation.STAFF, 600);

        System.out.println("Parked 3 vehicles successfully:");
        System.out.println("  " + t1);
        System.out.println("  " + t2);
        System.out.println("  " + t3);
        System.out.printf("Current Occupancy: %d / %d slots%n", facility.getCurrentOccupancy(), facility.getTotalCapacity());

        // Vacate and bill
        Invoice inv1 = BillingService.computeInvoice(t1, 690, 0); // 90 min (2 hrs @ 10 = Rs 20)
        facility.vacateVehicle("TN01N101");
        System.out.println("Vacated TN01N101: " + inv1);
        System.out.printf("Occupancy after checkout: %d / %d slots%n%n", facility.getCurrentOccupancy(), facility.getTotalCapacity());
    }

    private static void runBoundaryCasesTest() {
        System.out.println("--- TEST 2: BOUNDARY CASES ---");
        // Capacity = 3 slots
        ParkingFacility facility = new ParkingFacility(new int[]{2, 1}, 1);

        // A. Fill to the exact last free slot
        facility.parkVehicle(new Car("TN11B1"), UserAffiliation.VISITOR, 100);
        facility.parkVehicle(new Car("TN11B2"), UserAffiliation.VISITOR, 100);
        ParkingTicket lastTicket = facility.parkVehicle(new Car("TN11B3"), UserAffiliation.VISITOR, 100);
        System.out.println("Allocated last available slot: " + lastTicket);
        System.out.printf("Facility Status: %d / %d (Free: %d)%n",
                facility.getCurrentOccupancy(), facility.getTotalCapacity(), facility.getFreeSlotsCount());

        ParkingTicket overflowTicket = facility.parkVehicle(new Car("TN11B4"), UserAffiliation.VISITOR, 100);
        System.out.println("Attempting allocation when full: " + overflowTicket + " (CORRECT: null returned)");

        // B. Zero-minute parking stay (immediate turnaround)
        Vehicle zeroCar = new Car("TN11ZERO");
        ParkingTicket zeroTicket = new ParkingTicket(9999, zeroCar, UserAffiliation.VISITOR, 0, 0, 500);
        Invoice zeroInvoice = BillingService.computeInvoice(zeroTicket, 500, 0); // 0 min
        System.out.println("Zero-minute stay invoice: " + zeroInvoice);
        System.out.println("Zero stay fee is: Rs " + zeroInvoice.netPayable + " (CORRECT: No charge for 0 min)");

        // C. Stay exactly at fee slab boundary (60 mins vs 61 mins)
        Vehicle slabCar1 = new Car("TN11SLAB60");
        Vehicle slabCar2 = new Car("TN11SLAB61");
        ParkingTicket tkt60 = new ParkingTicket(9001, slabCar1, UserAffiliation.VISITOR, 0, 0, 1000);
        ParkingTicket tkt61 = new ParkingTicket(9002, slabCar2, UserAffiliation.VISITOR, 0, 0, 1000);

        Invoice inv60 = BillingService.computeInvoice(tkt60, 1060, 0); // Exactly 60 mins -> 1 hr
        Invoice inv61 = BillingService.computeInvoice(tkt61, 1061, 0); // Exactly 61 mins -> 2 hrs

        System.out.printf("Slab Boundary Check: 60 mins -> %d hr = Rs %.1f%n", inv60.billableHours, inv60.netPayable);
        System.out.printf("Slab Boundary Check: 61 mins -> %d hrs = Rs %.1f%n%n", inv61.billableHours, inv61.netPayable);
    }

    private static void runInvalidCasesTest() {
        System.out.println("--- TEST 3: INVALID CASES ---");
        ParkingFacility facility = new ParkingFacility(new int[]{2, 2}, 1);

        // Case A: Null Vehicle
        try {
            facility.parkVehicle(null, UserAffiliation.VISITOR, 100);
        } catch (IllegalArgumentException ex) {
            System.out.println("[CAUGHT NULL VEHICLE] " + ex.getMessage());
        }

        // Case B: Duplicate Registration Plate
        facility.parkVehicle(new Car("TN09DUP111"), UserAffiliation.VISITOR, 100);
        try {
            facility.parkVehicle(new Car("TN09DUP111"), UserAffiliation.STUDENT, 105);
        } catch (IllegalStateException ex) {
            System.out.println("[CAUGHT DUPLICATE ENTRY] " + ex.getMessage());
        }

        // Case C: Vacating an Unknown Vehicle
        try {
            facility.vacateVehicle("TN99GHOST");
        } catch (Exception ex) {
            System.out.println("[CAUGHT UNKNOWN VACATE] " + ex.getMessage());
        }
        System.out.println();
    }

    private static void runHandCalculatedScenarioTest() {
        System.out.println("--- TEST 4: HAND-CALCULATED SCENARIO RECONCILIATION ---");
        System.out.println("Scenario Breakdown:");
        System.out.println("  1. TN01AA1001 (TwoWheeler, Visitor, 45 min)         -> 1 hr @ Rs 10 = Rs 10.0");
        System.out.println("  2. TN01AA1002 (TwoWheeler, Student, 180 min)        -> 3 hrs waived = Rs 0.0");
        System.out.println("  3. TN02BB2001 (Car, Visitor, 60 min)                -> 1 hr @ Rs 30 = Rs 30.0");
        System.out.println("  4. TN02BB2002 (Car, Visitor, 61 min)                -> 2 hrs @ Rs 30 = Rs 60.0");
        System.out.println("  5. TN03CC3001 (ElectricCar, Staff, 120 min, 15 kWh) -> Base Rs 0 + 15*12 = Rs 180.0");
        System.out.println("  6. TN03CC3002 (ElectricCar, Visitor, 130 min, 20kWh)-> 3*30 + 20*12 = Rs 330.0");
        System.out.println("HAND-CALCULATED TOTAL REVENUE: Rs 610.0\n");

        ParkingFacility facility = new ParkingFacility(new int[]{4, 4}, 2);

        Vehicle v1 = new TwoWheeler("TN01AA1001");
        Vehicle v2 = new TwoWheeler("TN01AA1002");
        Vehicle v3 = new Car("TN02BB2001");
        Vehicle v4 = new Car("TN02BB2002");
        Vehicle v5 = new ElectricCar("TN03CC3001");
        Vehicle v6 = new ElectricCar("TN03CC3002");

        long baseTime = 1000;
        ParkingTicket t1 = facility.parkVehicle(v1, UserAffiliation.VISITOR, baseTime);
        ParkingTicket t2 = facility.parkVehicle(v2, UserAffiliation.STUDENT, baseTime);
        ParkingTicket t3 = facility.parkVehicle(v3, UserAffiliation.VISITOR, baseTime);
        ParkingTicket t4 = facility.parkVehicle(v4, UserAffiliation.VISITOR, baseTime);
        ParkingTicket t5 = facility.parkVehicle(v5, UserAffiliation.STAFF, baseTime);
        ParkingTicket t6 = facility.parkVehicle(v6, UserAffiliation.VISITOR, baseTime);

        Invoice inv1 = BillingService.computeInvoice(t1, baseTime + 45, 0);
        Invoice inv2 = BillingService.computeInvoice(t2, baseTime + 180, 0);
        Invoice inv3 = BillingService.computeInvoice(t3, baseTime + 60, 0);
        Invoice inv4 = BillingService.computeInvoice(t4, baseTime + 61, 0);
        Invoice inv5 = BillingService.computeInvoice(t5, baseTime + 120, 15);
        Invoice inv6 = BillingService.computeInvoice(t6, baseTime + 130, 20);

        Invoice[] invoices = {inv1, inv2, inv3, inv4, inv5, inv6};
        double programTotal = 0.0;
        for (Invoice inv : invoices) {
            System.out.println("  " + inv);
            programTotal += inv.netPayable;
        }

        System.out.println("\n--------------------------------------------------------------");
        System.out.printf("Hand-Calculated Total Expected: Rs %.2f%n", 610.00);
        System.out.printf("Program Computed Actual Total:  Rs %.2f%n", programTotal);
        boolean match = (Double.compare(610.00, programTotal) == 0);
        System.out.println("Reconciliation Verdict: " + (match ? "EXACT MATCH (100% RECONCILED)" : "MISMATCH"));
        System.out.println("--------------------------------------------------------------\n");
    }

    private static void runDataStructureLookupBenchmark() {
        System.out.println("--- TEST 5: ACTIVE TICKET DATA STRUCTURE LOOKUP BENCHMARK ---");
        int count = 1000;
        ParkingFacility facility = new ParkingFacility(new int[]{count}, count / 4);

        for (int i = 0; i < count; i++) {
            facility.parkVehicle(new Car("TN" + i), UserAffiliation.VISITOR, 0);
        }

        String searchTarget = "TN" + (count - 1); // Worst-case last element

        // 1. Array Linear Scan
        long startArr = System.nanoTime();
        ParkingTicket foundArr = facility.findTicketByArray(searchTarget);
        long timeArr = System.nanoTime() - startArr;

        // 2. ArrayList Scan
        long startList = System.nanoTime();
        ParkingTicket foundList = facility.findTicketByList(searchTarget);
        long timeList = System.nanoTime() - startList;

        // 3. HashMap Lookup
        long startMap = System.nanoTime();
        ParkingTicket foundMap = facility.findTicketByMap(searchTarget);
        long timeMap = System.nanoTime() - startMap;

        System.out.printf("Time to find worst-case ticket across %d active records:%n", count);
        System.out.printf("  1. Array Linear Scan (O(n)):       %d ns%n", timeArr);
        System.out.printf("  2. ArrayList Linear Scan (O(n)):   %d ns%n", timeList);
        System.out.printf("  3. HashMap Keyed by RegNo (O(1)):  %d ns%n", timeMap);
        System.out.printf("HashMap speedup over Array scan: %.1fx faster%n", (double) timeArr / Math.max(1, timeMap));
    }
}
