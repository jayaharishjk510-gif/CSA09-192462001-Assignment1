/**
 * Task 1: Understand the Problem and Identify the Objects
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * This program demonstrates:
 * 1. The Object-Oriented design vs Parallel Array approach for vehicle representation.
 * 2. Search efficiency by registration number.
 * 3. Ease of extending vehicle attributes.
 * 4. Memory footprint and structural integrity verification.
 */
public class ProblemAnalysisDemo {

    // --- Object-Oriented Representation ---
    static class VehicleObject {
        private final String regNo;
        private final String type;
        private final long entryTimeMinutes;
        // Adding a new attribute is cleanly encapsulated:
        private final boolean hasMonthlyPass;

        public VehicleObject(String regNo, String type, long entryTimeMinutes, boolean hasMonthlyPass) {
            this.regNo = regNo;
            this.type = type;
            this.entryTimeMinutes = entryTimeMinutes;
            this.hasMonthlyPass = hasMonthlyPass;
        }

        public String getRegNo() { return regNo; }
        public String getType() { return type; }
        public long getEntryTimeMinutes() { return entryTimeMinutes; }
        public boolean hasMonthlyPass() { return hasMonthlyPass; }

        @Override
        public String toString() {
            return String.format("Vehicle[regNo=%s, type=%s, entry=%d min, pass=%b]",
                    regNo, type, entryTimeMinutes, hasMonthlyPass);
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 1: OBJECT-ORIENTED MODEL VS PARALLEL ARRAYS COMPARISON");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        int n = 5;
        // 1. Parallel Array Representation
        String[] regNo = new String[n];
        String[] type = new String[n];
        long[] entryTime = new long[n];
        // To add a new attribute in parallel arrays, another parallel array MUST be allocated:
        boolean[] hasMonthlyPass = new boolean[n];

        // Populate parallel arrays
        String[] sampleRegs = {"TN01AB1111", "TN02CD2222", "TN03EF3333", "TN04GH4444", "TN05IJ5555"};
        String[] sampleTypes = {"TwoWheeler", "Car", "ElectricCar", "Car", "TwoWheeler"};
        long[] sampleEntries = {540, 560, 580, 600, 620};
        boolean[] samplePasses = {true, false, false, true, false};

        for (int i = 0; i < n; i++) {
            regNo[i] = sampleRegs[i];
            type[i] = sampleTypes[i];
            entryTime[i] = sampleEntries[i];
            hasMonthlyPass[i] = samplePasses[i];
        }

        // 2. Object-Oriented Representation
        VehicleObject[] vehicles = new VehicleObject[n];
        for (int i = 0; i < n; i++) {
            vehicles[i] = new VehicleObject(sampleRegs[i], sampleTypes[i], sampleEntries[i], samplePasses[i]);
        }

        // 3. Search Comparison: Finding vehicle "TN03EF3333"
        String targetReg = "TN03EF3333";
        System.out.println("--> Searching for vehicle: " + targetReg);

        // Linear Search in Parallel Arrays
        int foundIndex = -1;
        for (int i = 0; i < n; i++) {
            if (regNo[i] != null && regNo[i].equals(targetReg)) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex != -1) {
            System.out.printf("[Parallel Array Result] Found at index %d: Reg=%s, Type=%s, Entry=%d, Pass=%b%n",
                    foundIndex, regNo[foundIndex], type[foundIndex], entryTime[foundIndex], hasMonthlyPass[foundIndex]);
        }

        // Linear Search in Object Array
        VehicleObject foundVehicle = null;
        for (VehicleObject v : vehicles) {
            if (v != null && v.getRegNo().equals(targetReg)) {
                foundVehicle = v;
                break;
            }
        }
        if (foundVehicle != null) {
            System.out.println("[Object Model Result] Found: " + foundVehicle);
        }

        // 4. Structural Integrity & Risk Analysis
        System.out.println("\n--- Structural Risk Simulation in Parallel Arrays ---");
        System.out.println("Notice: If one array is sorted or updated without updating all companion arrays,");
        System.out.println("data becomes corrupt. In Object-Oriented design, state is atomic and encapsulated.");
        System.out.println("Vehicle search time complexity: O(n) for unsorted array, O(1) when using Map/HashSet.");
        System.out.println("========================================================================");
    }
}
