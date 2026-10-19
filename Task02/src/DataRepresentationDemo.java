import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Task 2: Choose the Data Representation
 * Student: Jaya Harish J (Reg No: 192462001)
 *
 * This program demonstrates:
 * 1. Data types chosen for parking fields and their justifications.
 * 2. Jagged 2D Array vs Flattened 1D Array vs ArrayList with variable floor capacities.
 * 3. Type conversion, integer division pitfalls, and ceiling billing accuracy.
 * 4. Input processing via Scanner and Command Line Arguments.
 */
public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("TASK 2: DATA REPRESENTATION, PRECISION LOSS & ARRAY ARCHITECTURE");
        System.out.println("Student: Jaya Harish J | Reg No: 192462001");
        System.out.println("========================================================================\n");

        // 1. Precision Loss & Fee Calculation Demo
        System.out.println("--- 1. Precision Loss & Integer Division Pitfall Demo ---");
        int parkedMinutes = 65; // 1 hour and 5 minutes
        double hourlyRate = 30.0;

        // WRONG METHOD 1: Plain integer division (truncates fraction)
        int wrongHours1 = parkedMinutes / 60; // 65 / 60 = 1
        double wrongFee1 = wrongHours1 * hourlyRate; // 1 * 30.0 = 30.0 (Undercharges customer by 1 hr!)

        // WRONG METHOD 2: Truncating cast on floating division
        int wrongHours2 = (int) (parkedMinutes / 60.0); // (int) 1.0833 = 1
        double wrongFee2 = wrongHours2 * hourlyRate;

        // CORRECT METHOD 1: Integer ceiling arithmetic (No float precision issues)
        int correctHoursInteger = (parkedMinutes + 59) / 60; // (65 + 59) / 60 = 124 / 60 = 2
        double correctFee1 = correctHoursInteger * hourlyRate; // 2 * 30.0 = 60.0

        // CORRECT METHOD 2: Math.ceil with double casting
        int correctHoursCeil = (int) Math.ceil((double) parkedMinutes / 60.0);
        double correctFee2 = correctHoursCeil * hourlyRate;

        System.out.println("Duration: " + parkedMinutes + " minutes | Rate: Rs " + hourlyRate + "/hr");
        System.out.println("WRONG Fee (Integer Division `minutes / 60`): Rs " + wrongFee1 + " [REVENUE LOSS: Rs 30.0]");
        System.out.println("WRONG Fee (Casting `(int)(minutes / 60.0)`):  Rs " + wrongFee2 + " [REVENUE LOSS: Rs 30.0]");
        System.out.println("CORRECT Fee (Ceiling `(minutes + 59) / 60`): Rs " + correctFee1 + " [VALID BILLING]");
        System.out.println("CORRECT Fee (Math.ceil):                     Rs " + correctFee2 + " [VALID BILLING]");

        // 2. Variable Floor Array Representation Demo
        System.out.println("\n--- 2. Array Representations for Variable Floor Slots ---");
        // Floor 0 has 3 slots, Floor 1 has 2 slots, Floor 2 has 4 slots (Non-uniform)
        int[] slotsPerFloor = {3, 2, 4};
        int numFloors = slotsPerFloor.length;

        // A. Jagged 2D Array
        String[][] jaggedSlots = new String[numFloors][];
        for (int f = 0; f < numFloors; f++) {
            jaggedSlots[f] = new String[slotsPerFloor[f]];
        }
        jaggedSlots[0][1] = "TN01AA1111";
        jaggedSlots[2][3] = "TN03CC3333";
        System.out.println("Jagged 2D Array Structure:");
        for (int f = 0; f < jaggedSlots.length; f++) {
            System.out.println("  Floor " + f + " (Capacity " + jaggedSlots[f].length + "): " + Arrays.toString(jaggedSlots[f]));
        }

        // B. Flattened 1D Array with Prefix Offset Array (Handles Variable Floor Sizes)
        int totalSlots = 0;
        int[] floorOffsets = new int[numFloors];
        for (int f = 0; f < numFloors; f++) {
            floorOffsets[f] = totalSlots;
            totalSlots += slotsPerFloor[f];
        }
        String[] flatSlots = new String[totalSlots];
        // Accessing floor 0, slot 1:
        flatSlots[floorOffsets[0] + 1] = "TN01AA1111";
        // Accessing floor 2, slot 3:
        flatSlots[floorOffsets[2] + 3] = "TN03CC3333";
        System.out.println("\nFlattened 1D Array with Prefix Offsets (Total size " + totalSlots + "):");
        System.out.println("  " + Arrays.toString(flatSlots));

        // C. ArrayList<String[]> Representation
        ArrayList<String[]> listSlots = new ArrayList<>();
        for (int f = 0; f < numFloors; f++) {
            listSlots.add(new String[slotsPerFloor[f]]);
        }
        listSlots.get(0)[1] = "TN01AA1111";
        listSlots.get(2)[3] = "TN03CC3333";
        System.out.println("\nArrayList<String[]> Representation:");
        for (int f = 0; f < listSlots.size(); f++) {
            System.out.println("  Floor " + f + ": " + Arrays.toString(listSlots.get(f)));
        }

        // 3. Simulated Input Reading Demo (via Scanner)
        System.out.println("\n--- 3. Input Strategy Demonstration ---");
        String mockInput = "TN05ZZ9999 Car 125\n";
        Scanner sc = new Scanner(mockInput);
        String reg = sc.next();
        String type = sc.next();
        int mins = sc.nextInt();
        sc.close();
        System.out.printf("Parsed Scanner Input -> RegNo: %s, Type: %s, Minutes: %d%n", reg, type, mins);
        System.out.println("========================================================================");
    }
}
