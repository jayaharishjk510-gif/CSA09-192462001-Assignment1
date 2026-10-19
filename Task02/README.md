# Task 2 – Choose the Data Representation
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 3 Marks  

---

## 1. Field Data Type Selection and Justification

| Field Name | Data Type Chosen | Justification & Architectural Rationale |
| :--- | :--- | :--- |
| `floorNumber` | `int` | Integer values ($0, 1, \dots, F-1$). Directly maps to Java array indices with zero conversion overhead. |
| `slotNumber` | `int` | 0-indexed integer identifying the bay within a floor. Compact primitive representation (32-bit). |
| `vehicleNumber` (`regNo`) | `String` | Vehicle registration numbers are alphanumeric strings (e.g., `"TN01AB1111"`). Immutable, thread-safe, and natively supports regex validation and hashing. |
| `entryTime` / `exitTime` | `LocalDateTime` (or `long`) | Standard `java.time` API provides immutable, ISO-8601 compliant timestamps with daylight saving and duration arithmetic. In lightweight simulations, `long` epoch minutes prevent timezone edge cases. |
| `durationMinutes` | `int` or `long` | Minutes elapsed between entry and checkout. Represented as an integer primitive to avoid floating point representation errors and drift during arithmetic. |
| `fee` | `double` | Currency amount. While monetary financial ledgers prefer `BigDecimal` for arbitrary scale, `double` is chosen for core calculations with strict integer ceiling rules to guarantee accurate currency computation without floating precision loss. |

---

## 2. Floor and Slot Representation: Jagged 2D Array vs Alternatives

### Why a Jagged 2D Array (`String[][]` or `ParkingSlot[][]`) is Chosen:
In real campus buildings, civil infrastructure rarely yields identical slot counts on every floor:
- Ground floor often hosts security booths, administrative checkpoints, and ramps (fewer parking slots, e.g., 20 slots).
- Intermediate floors have regular uniform bays (e.g., 50 slots).
- Basement floors or rooftop open bays have different layouts (e.g., 40 slots).

A **Jagged 2D Array** represents each floor as an independent 1D array of distinct length:
```java
String[][] slots = new String[slotsPerFloor.length][];
for (int f = 0; f < slotsPerFloor.length; f++) {
    slots[f] = new String[slotsPerFloor[f]];
}
```
**Benefits:**
1. **Zero Wasted Memory:** Exactly $\sum_{f=0}^{F-1} S_f$ references are allocated.
2. **Bounds Protection:** Each floor's array length is dynamically respected (`slots[f].length`). Attempting to access an illegal slot on a shorter floor naturally triggers an `ArrayIndexOutOfBoundsException` or can be validated directly.
3. **Intuitive Addressing:** Slots are addressed using direct Cartesian coordinates: `slots[floor][slot]`.

---

## 3. Type Conversion, Casting Risks & Precision Loss

### The Integer Division Problem
In Java, dividing two integers performs mathematical truncation towards zero (floor division for positive values).
Suppose a vehicle is parked for **65 minutes** (1 hour and 5 minutes), and the hourly parking tariff is **Rs 30.0/hr**.

#### Flawed Approach 1: Plain Integer Division
```java
int hours = minutes / 60; // 65 / 60 yields 1 (truncated!)
double fee = hours * 30.0; // 1 * 30.0 = Rs 30.0 (LOSS: Customer undercharged by Rs 30.0!)
```

#### Flawed Approach 2: Truncating Cast on Floating Division
```java
int hours = (int) (minutes / 60.0); // (int) 1.083333 yields 1
double fee = hours * 30.0; // Undercharges the customer!
```

#### Correct Approach 1: Integer Ceiling Arithmetic (Recommended)
By adding 59 prior to integer division, any fraction of an hour automatically rolls over to the next full integer hour using pure integer operations (zero floating-point hardware overhead):
```java
int hours = (minutes + 59) / 60; // (65 + 59) / 60 = 124 / 60 = 2 hours
double fee = hours * 30.0; // 2 * 30.0 = Rs 60.0 (CORRECT)
```
*Verification:*
- If $minutes = 60 \implies (60 + 59) / 60 = 119 / 60 = 1$ hour.
- If $minutes = 61 \implies (61 + 59) / 60 = 120 / 60 = 2$ hours.
- If $minutes = 0 \implies (0 + 59) / 60 = 0$ hours (or handled via condition).

#### Correct Approach 2: Explicit Math Ceiling
```java
int hours = (int) Math.ceil((double) minutes / 60.0);
```

---

## 4. Input Reading Strategy
The system supports dual input strategies:
1. **Interactive Console Input via `java.util.Scanner`**:
   Reads user input interactively with robust validation (`hasNextInt()`, `hasNext()`) preventing unhandled input format exceptions.
2. **Automated Batch Processing via Command-Line Arguments**:
   Accepts parameters directly from CLI or test harnesses (e.g., `args[0] = regNo`, `args[1] = type`, `args[2] = minutes`), allowing fully automated grading and stress testing without terminal blocking.

---

## 5. Alternative Concept and Complexity Analysis

### Comparison: Jagged 2D Array vs Flattened 1D Array vs `ArrayList<String[]>`

Suppose the facility has $F$ floors, where floor $f$ has $S_f$ slots.

#### 1. Flattened 1D Array (`index = floor * slotsPerFloor + slot`):
- **When all floors are equal ($S_f = S$):** Simple formula works. Locating slot is $O(1)$ arithmetic.
- **When floors have different numbers of slots ($S_f$ variable):** The fixed formula `floor * slotsPerFloor` breaks down. One must maintain a **prefix sum array** `prefixSum[f] = \sum_{i=0}^{f-1} S_i`. The slot index becomes `index = prefixSum[floor] + slot`.
- **Memory Overhead:** A uniform 1D array sized for $\max(S_f)$ wastes $(F \times \max(S_f) - \sum S_f)$ array slots filled with nulls.

#### 2. `ArrayList<String[]>`:
- Holds an `ArrayList` where each element is an array `String[S_f]`.
- Accessing a slot requires an `ArrayList.get(floor)` method call followed by array indexing `[slot]`.
- Has extra pointer chasing and dynamic resizing capacity overhead.

#### Complexity & Evaluation Summary:

| Architecture | Time to Locate (`floor, slot`) | Memory Allocation | Scalability to Variable Floors |
| :--- | :--- | :--- | :--- |
| **Jagged Array `String[F][]`** | $O(1)$ direct double dereference | $\sum S_f + F$ references | **Native & Optimal** |
| **Flattened 1D Array (Prefix)** | $O(1)$ arithmetic + 1 array lookup | $\sum S_f$ references + prefix array | Rigid; requires index translation |
| **Flattened 1D Array (Max S)** | $O(1)$ arithmetic | $F \times \max(S_f)$ references | **Wastes memory** with unused null entries |
| **`ArrayList<String[]>`** | $O(1)$ via method dispatch | $\sum S_f + F + \text{List overhead}$ | Flexible, slight object overhead |

---

## 6. Execution and Verification
To compile and execute the Task 2 demonstration:
```bash
javac Task02/src/DataRepresentationDemo.java
java -cp Task02/src DataRepresentationDemo
```
Refer to `output.txt` for the recorded terminal execution results.
