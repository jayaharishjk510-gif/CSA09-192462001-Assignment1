# Task 3 – Design the Control Logic
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4 Marks  

---

## 1. Decision and Looping Logic Analysis

### 1. Slot Allocation (`allocate`)
- **Search Strategy:** Nested iteration over structural dimensions. Outer loop iterates sequentially through floors $f \in [0, \text{slots.length}-1]$; inner loop iterates through slots $s \in [0, \text{slots}[f].\text{length}-1]$.
- **Early Termination via `return`:** The moment an empty slot (`slots[f][s] == null`) is detected, the vehicle registration number is assigned, and execution returns immediately (`return new int[]{f, s};`). This avoids checking any subsequent slots or floors.
- **Facility Full Handling:** If both loops terminate without discovering a null slot, the method exits with `return null;`.

### 2. Slot Release (`release`)
- **Boundary Validation:** Prior to any array dereference, array boundary bounds checks guard against `ArrayIndexOutOfBoundsException`:
  `floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length`.
- **Occupancy Validation:** If `slots[floor][slot] == null`, the slot is already empty; method returns `false` without state mutation.
- **State Mutation:** If valid and occupied, the slot is cleared (`slots[floor][slot] = null;`) and returns `true`.

### 3. Fee Slab Decision Logic
Fee computations apply progressive slab pricing:
- First hour: Fixed base rate ($R_1$).
- Subsequent hours: Discounted or standard hourly rate ($R_2$).
- Daily maximum cap: If calculated fee exceeds $M_{max}$, the fee is capped at $M_{max}$.
- Implemented via chained `if-else` or ternary ceiling conditions to avoid evaluating irrelevant lower slabs.

---

## 2. Algorithmic Pseudocode (SIMATS Engineering Standard)

### Algorithm 1: Slot Allocation
```text
ALGORITHM AllocateSlot(slots, vehicleNo)
    // Input: slots: 2D Jagged Array of Strings [0..F-1][0..S_f-1]
    //        vehicleNo: String representing vehicle registration number
    // Output: Array [floor, slot] of allocated position, or NULL if full
    // Precondition: slots is initialized; vehicleNo is non-null
    // Postcondition: First available slot contains vehicleNo; position returned

    FOR f ← 0 TO LENGTH(slots) - 1 DO
        FOR s ← 0 TO LENGTH(slots[f]) - 1 DO
            IF slots[f][s] IS NULL THEN
                slots[f][s] ← vehicleNo
                RETURN [f, s]
            END IF
        END FOR
    END FOR

    RETURN NULL
END ALGORITHM
```

### Algorithm 2: Slot Release
```text
ALGORITHM ReleaseSlot(slots, floor, slot)
    // Input: slots: 2D Jagged Array of Strings
    //        floor: Integer floor index
    //        slot: Integer slot index
    // Output: Boolean TRUE if successfully released, FALSE otherwise
    // Precondition: slots is initialized
    // Postcondition: If valid and occupied, slots[floor][slot] is set to NULL

    IF floor < 0 OR floor >= LENGTH(slots) THEN
        RETURN FALSE
    END IF

    IF slot < 0 OR slot >= LENGTH(slots[floor]) THEN
        RETURN FALSE
    END IF

    IF slots[floor][slot] IS NULL THEN
        RETURN FALSE
    END IF

    slots[floor][slot] ← NULL
    RETURN TRUE
END ALGORITHM
```

### Algorithm 3: Parking Fee Calculation with Slabs
```text
ALGORITHM CalculateParkingFee(minutes, ratePerHour, hasPass)
    // Input: minutes: Non-negative integer parking duration
    //        ratePerHour: Positive real number
    //        hasPass: Boolean pass status
    // Output: Total fee as real number

    IF hasPass IS TRUE THEN
        RETURN 0.0
    END IF

    IF minutes <= 0 THEN
        RETURN 0.0
    END IF

    // Ceiling rounding using pure integer arithmetic
    hours ← (minutes + 59) DIV 60

    IF hours == 1 THEN
        baseFee ← ratePerHour
    ELSE IF hours <= 6 THEN
        baseFee ← ratePerHour + (hours - 1) * (ratePerHour * 0.8)
    ELSE
        baseFee ← ratePerHour * 5.0  // Daily capped rate
    END IF

    RETURN baseFee
END ALGORITHM
```

---

## 3. Alternative Concept and Complexity Analysis

### Comparison: Linear Scan vs Stack-Based Free Slot Pool
In the standard design, `allocate()` performs a 2D linear scan across floors and slots.
An alternative architectural pattern maintains:
1. An integer counter of free slots per floor: `int[] freeSlotsPerFloor`.
2. A LIFO Stack (or FIFO Queue) of free slot coordinates: `Deque<SlotPosition> freeSlotStack`.

#### Operational Flow in Stack Approach:
- **Initialization:** Push all $[f, s]$ coordinates onto the stack ($O(F \times S)$ one-time setup).
- **`allocate(vehicleNo)`:**
  - Check if `freeSlotStack.isEmpty()`.
  - Pop position $[f, s]$ in $O(1)$ constant time.
  - Assign vehicle and decrement floor counter in $O(1)$.
- **`release(floor, slot)`:**
  - Validate bounds and occupancy.
  - Set `slots[floor][slot] = null`.
  - Push $[floor, slot]$ back onto `freeSlotStack` in $O(1)$ constant time.

#### Time and Space Complexity Comparison ($F$ floors, $S$ slots per floor):

| Metric | Linear Scan Design (`ParkingFacility`) | Stack-Based Free Pool (`OptimizedParkingFacility`) |
| :--- | :--- | :--- |
| **`allocate()` Best Case** | $O(1)$ (First slot on Floor 0 is free) | $O(1)$ |
| **`allocate()` Worst Case** | $O(F \times S)$ (Facility nearly full or completely full) | **$O(1)$ Constant Time** |
| **`allocate()` Average Case** | $O(\frac{F \times S}{2})$ | **$O(1)$ Constant Time** |
| **`release()` Time** | $O(1)$ (Direct indexed check) | $O(1)$ (Push to stack) |
| **Extra Space Needed** | $O(1)$ (No auxiliary data structures) | $O(F \times S)$ references for coordinate wrappers |
| **Code Simplicity** | Minimal footprint, zero caching hazards | Requires cache synchronization on release |

---

## 4. Execution and Verification
To compile and execute Task 3:
```bash
javac Task03/src/ParkingFacility.java Task03/src/OptimizedParkingFacility.java
java -cp Task03/src ParkingFacility
java -cp Task03/src OptimizedParkingFacility
```

### Verified Terminal Output:
```text
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```
The output strictly matches the university expected output.
