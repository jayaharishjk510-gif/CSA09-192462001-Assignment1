import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Task 7: Override and Specialise Behaviour
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Demonstrates:
 * 1. Method overriding with @Override annotation and rules (covariance, access).
 * 2. Safe use of `final` for immutability, template methods, and classes.
 * 3. Overriding toString(), equals(), and hashCode() adhering to the Object contract.
 * 4. Deduplication in Hash-based collections using equals/hashCode.
 * 5. Polymorphic overriding vs `switch` statement alternative analysis.
 */
public class MethodOverridingDemo {

    public static abstract class Vehicle {
        // 'final' variable: Identity cannot be tampered with after construction
        private final String regNo;

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            this.regNo = regNo.toUpperCase().trim();
        }

        public final String getRegNo() { return regNo; }

        // Abstract method: Enforces subclass specialization
        protected abstract double ratePerHour();

        // 'final' method: Template Method Pattern. Subclasses CANNOT alter billing arithmetic!
        public final double calculateFee(int minutes) {
            if (minutes <= 0) return 0.0;
            int hours = (minutes + 59) / 60; // Ceiling billing
            return hours * ratePerHour();
        }

        // Object Class Overrides: Logical equality based on registration number
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vehicle other)) return false;
            return Objects.equals(this.regNo, other.regNo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regNo);
        }

        @Override
        public String toString() {
            return String.format("%s [RegNo=%s, Rate=Rs %.2f/hr]",
                    getClass().getSimpleName(), regNo, ratePerHour());
        }
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() {
            return 10.0; // Two-wheeler rate
        }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() {
            return 30.0; // Standard 4-wheeler rate
        }
    }

    // 'final' class: Prevents uncontrolled further subclassing
    public static final class ElectricCar extends Car {
        private final int batteryKWh;

        public ElectricCar(String regNo, int batteryKWh) {
            super(regNo);
            this.batteryKWh = batteryKWh;
        }

        public double chargingFee(int kWh) { return kWh * 12.0; }

        @Override
        public String toString() {
            return String.format("%s [RegNo=%s, Battery=%d kWh, Rate=Rs %.2f/hr]",
                    getClass().getSimpleName(), getRegNo(), batteryKWh, ratePerHour());
        }
    }

    // --- Alternative Approach: Single method with switch on type enum ---
    enum VehicleEnum { TWO_WHEELER, CAR, ELECTRIC_CAR }

    static class SwitchBasedFeeCalculator {
        public static double calculateFee(VehicleEnum type, int minutes) {
            int hours = (minutes + 59) / 60;
            return switch (type) {
                case TWO_WHEELER -> hours * 10.0;
                case CAR -> hours * 30.0;
                case ELECTRIC_CAR -> hours * 30.0;
            };
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 7: METHOD OVERRIDING, FINAL MODIFIER & OBJECT CONTRACT");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        Vehicle v1 = new TwoWheeler("TN01AA1111");
        Vehicle v2 = new Car("TN02BB2222");
        Vehicle v3 = new ElectricCar("TN03CC3333", 75);
        Vehicle vDuplicate = new Car("TN01AA1111"); // Same regNo as v1, but different vehicle class

        System.out.println("--- 1. Overridden toString() Outputs ---");
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);

        System.out.println("\n--- 2. Object Contract: equals() and hashCode() Verification ---");
        System.out.println("v1.equals(vDuplicate): " + v1.equals(vDuplicate) + " (Both hold 'TN01AA1111')");
        System.out.println("v1.hashCode() == vDuplicate.hashCode(): " + (v1.hashCode() == vDuplicate.hashCode()));

        // Collection Deduplication Verification
        Set<Vehicle> activeParkedSet = new HashSet<>();
        activeParkedSet.add(v1);
        boolean addedDuplicate = activeParkedSet.add(vDuplicate);
        System.out.println("Adding v1 to HashSet: Success");
        System.out.println("Adding duplicate RegNo vDuplicate to HashSet: " + addedDuplicate + " (Rejected!)");
        System.out.println("HashSet size (Should be 1): " + activeParkedSet.size());

        System.out.println("\n--- 3. Overridden calculateFee() via Template Method Pattern ---");
        int testMinutes = 130; // 3 hours ceiling
        System.out.printf("%s 130 mins Fee: Rs %.2f%n", v1.getRegNo(), v1.calculateFee(testMinutes));
        System.out.printf("%s 130 mins Fee: Rs %.2f%n", v2.getRegNo(), v2.calculateFee(testMinutes));
        System.out.printf("%s 130 mins Fee: Rs %.2f%n", v3.getRegNo(), v3.calculateFee(testMinutes));

        System.out.println("\n--- 4. Switch Alternative Verification ---");
        double switchFee = SwitchBasedFeeCalculator.calculateFee(VehicleEnum.CAR, testMinutes);
        System.out.printf("Switch calculated fee for Car (130 mins): Rs %.2f%n", switchFee);
        System.out.println("Notice: If a new vehicle type is added, Switch requires modifying the enum,");
        System.out.println("recompiling the calculator, and updating every switch in the entire codebase!");
        System.out.println("========================================================================");
    }
}
