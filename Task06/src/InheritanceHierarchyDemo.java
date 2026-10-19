/**
 * Task 6: Design the Inheritance Hierarchy
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * Demonstrates:
 * 1. Single, Hierarchical, and Multilevel Inheritance for Vehicles and Users.
 * 2. Constructor execution ordering trace from base class to specialized subclasses.
 * 3. Use of `super` for constructor delegation and method reuse.
 * 4. Composition alternative (Vehicle has-a VehicleType) vs Inheritance.
 */
public class InheritanceHierarchyDemo {

    // --- Vehicle Inheritance Hierarchy ---
    public static abstract class Vehicle {
        private final String regNo;

        public Vehicle(String regNo) {
            System.out.println("  [1] Vehicle constructor executed for: " + regNo);
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }
        public abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) {
            super(regNo);
            System.out.println("  [2] TwoWheeler constructor executed.");
        }

        @Override
        public double ratePerHour() { return 10.0; }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) {
            super(regNo);
            System.out.println("  [2] Car constructor executed.");
        }

        @Override
        public double ratePerHour() { return 30.0; }
    }

    public static class ElectricCar extends Car {
        private final int batteryCapacityKWh;

        public ElectricCar(String regNo, int batteryCapacityKWh) {
            super(regNo); // Calls Car constructor, which in turn calls Vehicle constructor
            System.out.println("  [3] ElectricCar constructor executed with battery capacity: "
                    + batteryCapacityKWh + " kWh.");
            this.batteryCapacityKWh = batteryCapacityKWh;
        }

        public int getBatteryCapacityKWh() { return batteryCapacityKWh; }
        public double chargingFee(int kWh) { return kWh * 12.0; }
    }

    // --- User Affiliation Hierarchy ---
    public static abstract class CampusUser {
        private final String userId;
        private final String name;

        public CampusUser(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }
        public String getUserId() { return userId; }
        public String getName() { return name; }
        public abstract boolean hasFreeParkingPass();
    }

    public static class StudentUser extends CampusUser {
        public StudentUser(String userId, String name) { super(userId, name); }
        @Override public boolean hasFreeParkingPass() { return true; } // Monthly pass included
    }

    public static class StaffUser extends CampusUser {
        public StaffUser(String userId, String name) { super(userId, name); }
        @Override public boolean hasFreeParkingPass() { return true; } // Staff pass included
    }

    public static class VisitorUser extends CampusUser {
        public VisitorUser(String userId, String name) { super(userId, name); }
        @Override public boolean hasFreeParkingPass() { return false; } // Must pay
    }

    // --- Composition Alternative: Vehicle has-a VehicleType ---
    public static class VehicleType {
        private final String categoryName;
        private final double ratePerHour;
        private final boolean isElectric;

        public VehicleType(String categoryName, double ratePerHour, boolean isElectric) {
            this.categoryName = categoryName;
            this.ratePerHour = ratePerHour;
            this.isElectric = isElectric;
        }
        public String getCategoryName() { return categoryName; }
        public double getRatePerHour() { return ratePerHour; }
        public boolean isElectric() { return isElectric; }
    }

    public static class ComposedVehicle {
        private final String regNo;
        private final VehicleType type; // HAS-A relationship

        public ComposedVehicle(String regNo, VehicleType type) {
            this.regNo = regNo;
            this.type = type;
        }

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * type.getRatePerHour();
        }
        public String getRegNo() { return regNo; }
        public VehicleType getType() { return type; }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 6: INHERITANCE HIERARCHY & CONSTRUCTOR TRACE");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        System.out.println("--- 1. Constructor Execution Order Trace for ElectricCar ---");
        System.out.println("Instantiating: new ElectricCar(\"TN03C3\", 60):");
        ElectricCar ev = new ElectricCar("TN03C3", 60);
        System.out.println("Instantiation complete.\n");

        System.out.println("--- 2. Fee Calculation via Inheritance Hierarchy ---");
        Vehicle[] fleet = {
                new TwoWheeler("TN01A1"),
                new Car("TN02B2"),
                ev
        };

        for (Vehicle v : fleet) {
            int testMinutes = 120; // 2 hours
            System.out.printf("Vehicle: %s [%s] -> 2hr Fee: Rs %.2f%n",
                    v.getRegNo(), v.getClass().getSimpleName(), v.calculateFee(testMinutes));
        }

        System.out.println("\n--- 3. Composition Alternative Verification ---");
        VehicleType eCarType = new VehicleType("ElectricCar", 30.0, true);
        ComposedVehicle compCar = new ComposedVehicle("TN03C3-COMP", eCarType);
        System.out.printf("Composed Vehicle [%s, %s] -> 2hr Fee: Rs %.2f%n",
                compCar.getRegNo(), compCar.getType().getCategoryName(), compCar.calculateFee(120));

        System.out.println("\n--- 4. User Affiliation Pass Verification ---");
        CampusUser student = new StudentUser("S101", "Kavya");
        CampusUser visitor = new VisitorUser("V202", "Mr. Rajesh");
        System.out.printf("Student %s pass status: %b (Free Parking)%n", student.getName(), student.hasFreeParkingPass());
        System.out.printf("Visitor %s pass status: %b (Subject to full tariff)%n", visitor.getName(), visitor.hasFreeParkingPass());
        System.out.println("========================================================================");
    }
}
