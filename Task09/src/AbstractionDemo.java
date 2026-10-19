/**
 * Task 9: Introduce Abstraction (Abstract Classes vs Interfaces)
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Demonstrates:
 * 1. Abstract Class (Vehicle) for core identity, state, and partial template implementation.
 * 2. Interfaces (Chargeable, Payable, Trackable) for cross-cutting capabilities.
 * 3. Multiple inheritance of type via interfaces in ElectricCar.
 * 4. Default and Static methods in interfaces.
 * 5. Inversion analysis: What happens if Vehicle becomes an interface and Chargeable an abstract class?
 */
public class AbstractionDemo {

    // --- Interfaces for Cross-Cutting Capabilities ---
    public interface Chargeable {
        double chargingFee(int kWh);

        // Default method: Backward-compatible extension
        default double getEcoDiscountPercentage() {
            return 5.0; // 5% green discount for electric vehicle users
        }

        // Static utility method in interface
        static double getStandardUnitTariff() {
            return 12.0; // Standard Rs 12 per kWh
        }
    }

    public interface Trackable {
        String getSlotTelemetryCoordinates();
    }

    public interface Payable {
        double computeFinalPayable(double baseAmount);
    }

    // --- Abstract Class for Domain Entity ---
    public static abstract class Vehicle {
        // State (Instance Variables) - Interfaces CANNOT have instance state!
        private final String regNo;

        // Constructor - Interfaces CANNOT have constructors!
        protected Vehicle(String regNo) {
            this.regNo = regNo;
        }

        public final String getRegNo() { return regNo; }

        // Abstract method: What must be done (Subclasses decide how)
        protected abstract double ratePerHour();

        // Concrete template method: Shared behavior
        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 30.0; }
    }

    // ElectricCar achieves multiple behaviors: Inherits Car, implements Chargeable and Trackable
    public static class ElectricCar extends Car implements Chargeable, Trackable {
        public ElectricCar(String regNo) { super(regNo); }

        @Override
        public double chargingFee(int kWh) {
            return kWh * Chargeable.getStandardUnitTariff();
        }

        @Override
        public String getSlotTelemetryCoordinates() {
            return "EV-Bay: Floor 0, Bay 5 [Charging Port ACTIVE]";
        }
    }

    // User Implementing Payable Interface
    public static class CampusDriver implements Payable {
        private final String name;
        private final boolean isPassHolder;

        public CampusDriver(String name, boolean isPassHolder) {
            this.name = name;
            this.isPassHolder = isPassHolder;
        }

        @Override
        public double computeFinalPayable(double baseAmount) {
            return isPassHolder ? 0.0 : baseAmount;
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 9: ABSTRACTION - ABSTRACT CLASSES & INTERFACES");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        ElectricCar ev = new ElectricCar("TN09EV9999");
        System.out.println("--- 1. Multi-Capability Object (ElectricCar) ---");
        System.out.println("Vehicle Reg: " + ev.getRegNo());
        System.out.println("Parking Fee (120 min): Rs " + ev.calculateFee(120));
        System.out.println("Charging Fee (15 kWh): Rs " + ev.chargingFee(15));
        System.out.println("Default Eco Discount: " + ev.getEcoDiscountPercentage() + "%");
        System.out.println("Static Interface Tariff: Rs " + Chargeable.getStandardUnitTariff() + "/kWh");
        System.out.println("Telemetry: " + ev.getSlotTelemetryCoordinates());

        System.out.println("\n--- 2. Payable Interface Polymorphism ---");
        CampusDriver student = new CampusDriver("Harish", true);
        CampusDriver visitor = new CampusDriver("Mr. Sharma", false);
        double sampleFee = ev.calculateFee(120);

        System.out.printf("Student final payable for Rs %.2f bill: Rs %.2f (Pass Active)%n",
                sampleFee, student.computeFinalPayable(sampleFee));
        System.out.printf("Visitor final payable for Rs %.2f bill: Rs %.2f (Full Fee)%n",
                sampleFee, visitor.computeFinalPayable(sampleFee));

        System.out.println("\n--- 3. Architectural Inversion Analysis Summary ---");
        System.out.println("If Vehicle were an interface:");
        System.out.println("  1. Cannot declare instance variables (regNo) -> Every vehicle must duplicate state.");
        System.out.println("  2. Cannot have constructors -> Invariant checks cannot be centralized.");
        System.out.println("If Chargeable were an abstract class:");
        System.out.println("  1. ElectricCar cannot extend both Car AND Chargeable (Java forbids multiple class inheritance)!");
        System.out.println("========================================================================");
    }
}
