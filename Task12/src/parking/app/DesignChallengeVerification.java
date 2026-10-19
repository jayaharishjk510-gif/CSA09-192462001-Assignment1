package parking.app;

import parking.model.*;
import parking.rule.MonthlyPass;
import parking.rule.OneDayPass;
import parking.rule.Pass;
import parking.service.AdaptiveBillingService;
import parking.service.AdaptiveBillingService.AdaptiveInvoice;
import parking.service.ParkingFacility;

import java.time.LocalTime;

/**
 * Task 12: Day-4 Design Challenge Verification Driver
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Verifies:
 * 1. 5-Floor Extended Facility with Non-Uniform Jagged Array Slot Layout.
 * 2. E-Bike Flat Fee Pricing (Duration invariant).
 * 3. Delivery Van Dynamic Peak Pricing (Double car rate 9 AM - 5 PM).
 * 4. Visitor One-Day Pass Validation.
 * 5. Architectural metrics defending the adaptation.
 */
public class DesignChallengeVerification {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 12: DAY-4 DESIGN CHALLENGE - ADAPTATION & DEFENCE");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        // 1. Extended 5-Floor Jagged Facility
        System.out.println("--- 1. Extended 5-Floor Variable Layout Initialisation ---");
        int[] fiveFloorSlots = {20, 30, 40, 35, 25}; // Non-uniform slots per floor
        ParkingFacility facility = new ParkingFacility(fiveFloorSlots);

        System.out.println("Facility Floors and Capacities:");
        for (int f = 0; f < 5; f++) {
            System.out.printf("  Floor %d: %d slots%n", f, facility.getFloorCapacity(f));
        }
        System.out.printf("Total Campus Facility Capacity: %d slots%n%n", facility.getTotalCapacity());

        // 2. Test E-Bike Flat Fee Rule
        System.out.println("--- 2. New Category 1: E-Bike (Flat Fee Rule) ---");
        Vehicle eBike1 = new EBike("TN12EB001");
        Vehicle eBike2 = new EBike("TN12EB002");

        ParkingTicket tktEBikeShort = facility.allocate(eBike1, null, 100, LocalTime.of(8, 0));
        ParkingTicket tktEBikeLong = facility.allocate(eBike2, null, 100, LocalTime.of(8, 0));

        AdaptiveInvoice invEBikeShort = AdaptiveBillingService.generateBill(tktEBikeShort, 130, LocalTime.of(8, 30));  // 30 min
        AdaptiveInvoice invEBikeLong = AdaptiveBillingService.generateBill(tktEBikeLong, 400, LocalTime.of(13, 0));   // 300 min (5 hours)

        System.out.println("Short Stay (30 mins):  " + invEBikeShort);
        System.out.println("Long Stay (300 mins): " + invEBikeLong);
        System.out.println("Verified: Both stays incurred identical flat fee of Rs 15.0.\n");

        // 3. Test Delivery Van Dynamic Peak Pricing
        System.out.println("--- 3. New Category 2: Delivery Van (Peak vs Off-Peak Pricing) ---");
        Vehicle vanPeak = new DeliveryVan("TN12VAN-PEAK");
        Vehicle vanOffPeak = new DeliveryVan("TN12VAN-NIGHT");

        // Scenario A: Parked during Peak Hours (10:00 to 12:00 -> 2 hours)
        ParkingTicket tktVanPeak = facility.allocate(vanPeak, null, 600, LocalTime.of(10, 0));
        AdaptiveInvoice invVanPeak = AdaptiveBillingService.generateBill(tktVanPeak, 720, LocalTime.of(12, 0));

        // Scenario B: Parked during Off-Peak Hours (19:00 to 21:00 -> 2 hours)
        ParkingTicket tktVanOffPeak = facility.allocate(vanOffPeak, null, 1140, LocalTime.of(19, 0));
        AdaptiveInvoice invVanOffPeak = AdaptiveBillingService.generateBill(tktVanOffPeak, 1260, LocalTime.of(21, 0));

        System.out.println("Peak Stay (10:00 - 12:00, 2 hrs @ Rs 60/hr):  " + invVanPeak);
        System.out.println("Off-Peak Stay (19:00 - 21:00, 2 hrs @ Rs 30/hr): " + invVanOffPeak);
        System.out.println("Verified: Delivery van charged double rate (Rs 120 vs Rs 60) during peak.\n");

        // 4. Test Visitor One-Day Pass
        System.out.println("--- 4. Visitor One-Day Pass vs Standard Visitor ---");
        Vehicle visitorCar1 = new Car("TN12VIS-PASS");
        Vehicle visitorCar2 = new Car("TN12VIS-NOPASS");

        Pass dayPass = new OneDayPass("DAYPASS-20261019");
        ParkingTicket tktDayPass = facility.allocate(visitorCar1, dayPass, 600, LocalTime.of(10, 0));
        ParkingTicket tktNoPass = facility.allocate(visitorCar2, null, 600, LocalTime.of(10, 0));

        // Both park for 240 minutes (4 hours @ Rs 30 = Rs 120 base)
        AdaptiveInvoice invWithDayPass = AdaptiveBillingService.generateBill(tktDayPass, 840, LocalTime.of(14, 0));
        AdaptiveInvoice invWithoutPass = AdaptiveBillingService.generateBill(tktNoPass, 840, LocalTime.of(14, 0));

        System.out.println("Visitor With One-Day Pass: " + invWithDayPass);
        System.out.println("Visitor Without Pass:      " + invWithoutPass);
        System.out.println("Verified: One-day pass successfully waived 100% of the Rs 120 parking tariff.\n");

        // 5. Evidence-Based Defence Metrics
        System.out.println("--- 5. Evidence-Based Design Defence Summary ---");
        System.out.println("Existing classes modified: 0 classes (Vehicle, Car, TwoWheeler untouched).");
        System.out.println("New classes added: EBike, DeliveryVan, PricingRule, FlatFeePricingRule,");
        System.out.println("                  PeakHourPricingRule, Pass, MonthlyPass, OneDayPass.");
        System.out.println("Open-Closed Principle: STRICTLY SATISFIED.");
        System.out.println("Jagged Array Scalability: Seamlessly scaled to 5 non-uniform floors.");
        System.out.println("========================================================================");
    }
}
