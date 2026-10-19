# Task 1 – Understand the Problem and Identify the Objects
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4 Marks  

---

## 1. System Inputs and Outputs

### System Inputs
The Campus Smart-Parking System receives events and attributes representing vehicle entry and exit:
1. **Vehicle Registration Number (`regNo`)**: A unique alphanumeric state string (e.g., `"TN01AB1111"`).
2. **Vehicle Category**: Type of vehicle entered (`TwoWheeler`, `Car`, `ElectricCar`).
3. **User Category / Affiliation**: University affiliation (`Student`, `Staff`, `Visitor`).
4. **Pass Status / Pass ID**: Monthly pass credential verification for free parking eligibility.
5. **Entry Timestamp**: Time of entry recorded in hours and minutes or epoch time.
6. **Exit Timestamp**: Time of checkout for duration calculation.
7. **Charging Units Consumed (`kWh`)**: Electrical energy consumed if an Electric Vehicle uses a charging slot.

### System Outputs
1. **Parking Ticket**: Issued upon entry containing a unique ticket ID, allocated floor number, slot number, vehicle registration number, and entry timestamp.
2. **Parking Receipt / Invoice**: Issued upon exit displaying the checkout timestamp, total elapsed duration, billable hours (ceiling rounded), base parking fee, EV charging surcharge, pass discount/waiver, and net payable amount.
3. **Facility Status Reports**: Current occupancy per floor, total available slots, and slot allocation maps.

---

## 2. Noun-Verb Analysis

### Candidate Classes (From Nouns)
| Noun | Candidate Class | Justification |
| :--- | :--- | :--- |
| **Vehicle** | `Vehicle` (Abstract) | Encapsulates shared attributes (`regNo`, `entryTime`) and defines the polymorphic contract for fee calculation. |
| **Two-Wheeler** | `TwoWheeler` | Concrete specialization inheriting from `Vehicle` with distinct hourly pricing rate. |
| **Car** | `Car` | Concrete specialization representing standard 4-wheelers with specific slot dimension requirements and hourly rate. |
| **Electric Vehicle** | `ElectricCar` | Multilevel specialization of `Car` supporting the `Chargeable` capability. |
| **Parking Slot** | `ParkingSlot` | Represents an individual physical bay with floor number, slot number, slot type (Regular vs EV Charging), and occupancy status. |
| **Parking Floor** | `ParkingFloor` | Aggregates and manages a collection of parking slots on a specific structural level. |
| **Parking Facility** | `ParkingFacility` | Top-level coordinator managing all floors, executing allocation strategies, and releasing slots. |
| **Parking Ticket** | `ParkingTicket` | Immutable legal receipt binding a vehicle to an allocated slot with an entry timestamp. |
| **User** | `User` (and subclasses) | Represents the driver (`Student`, `Staff`, `Visitor`) to determine pass eligibility and fee waivers. |

### Candidate Methods (From Verbs)
| Verb | Candidate Method | Class | Purpose |
| :--- | :--- | :--- | :--- |
| **Allocate / Park** | `allocate(Vehicle v)` | `ParkingFacility` | Searches for the first suitable free slot and assigns the vehicle. |
| **Release / Vacate** | `release(int floor, int slot)` | `ParkingFacility` | Frees an occupied slot and marks it available for new arrivals. |
| **Calculate Fee** | `calculateFee(int minutes)` | `Vehicle` | Computes billable parking charges based on duration and vehicle rate. |
| **Recharge** | `chargingFee(int kWh)` | `Chargeable` interface | Calculates electrical consumption surcharge for EVs. |
| **Issue Ticket** | `issueTicket(Vehicle v, int floor, int slot)` | `ParkingFacility` | Generates a new `ParkingTicket` with a monotonic ticket sequence number. |
| **Validate Pass** | `hasValidPass()` | `User` / `ParkingTicket` | Verifies whether the parking fee should be waived. |

---

## 3. Class Responsibilities (CRC Model)

1. **`Vehicle`**:
   - *Responsibilities:* Maintain license number identity, enforce data integrity, provide polymorphic template for fee calculation.
   - *Collaborators:* `ParkingSlot`, `ParkingTicket`.
2. **`ParkingSlot`**:
   - *Responsibilities:* Maintain current occupancy state (`isOccupied`), track assigned vehicle reference, and report whether it supports EV charging.
   - *Collaborators:* `Vehicle`.
3. **`ParkingFacility`**:
   - *Responsibilities:* Manage multi-floor array structures, coordinate slot allocation and deallocation, prevent overbooking, track aggregate occupancy.
   - *Collaborators:* `ParkingFloor`, `ParkingSlot`, `ParkingTicket`.
4. **`ParkingTicket`**:
   - *Responsibilities:* Store immutable entry audit trail, generate monotonic ticket sequence numbers, compute billable duration.
   - *Collaborators:* `Vehicle`, `ParkingSlot`.

---

## 4. Business Rules
1. **Single Occupancy Invariant:** A slot cannot hold more than one vehicle at any given instant. A slot marked occupied cannot be re-allocated until an explicit `release()` occurs.
2. **Non-Reversible Ticket Identification:** Ticket IDs are monotonic, strictly increasing, and never reused across the lifecycle of the system.
3. **Billing Granularity:** Every started hour is billed in full (i.e., ceiling rounding: 1 minute into an hour counts as a full hour).
4. **Monthly Pass Exemption:** Registered Students and Staff with valid monthly credentials park free of base charge ($Rs\ 0.0$), but EV charging consumption is billed separately.
5. **EV Slot Compatibility:** Electric vehicles requiring charging must only be allocated slots designated as EV charging bays. Non-EVs must not block EV charging bays unless all regular bays are full and facility policy permits.

---

## 5. Facility Assumptions
1. Floors are 0-indexed ($0, 1, \dots, F-1$), and slots on each floor are 0-indexed ($0, 1, \dots, S_f-1$).
2. Floors may have variable slot capacities (non-uniform/jagged layout).
3. The facility operates continuously with automated entry barrier gates and ticket dispensers.

---

## 6. Alternative Concept and Complexity Analysis

### Comparison: Object-Oriented Model vs Parallel Arrays
If vehicles were represented using parallel arrays:
```java
String[] regNo = new String[MAX_CAPACITY];
String[] type = new String[MAX_CAPACITY];
long[] entryTime = new long[MAX_CAPACITY];
boolean[] hasMonthlyPass = new boolean[MAX_CAPACITY];
```

#### Finding a Vehicle by Registration Number:
- **Parallel Arrays:** A linear search across `regNo[]` must be performed. Once index `i` is found, properties are retrieved from `type[i]`, `entryTime[i]`, and `hasMonthlyPass[i]`.
  - Time Complexity: $O(n)$ comparisons.
- **Object-Oriented Model:** Vehicles are stored in an array or collection of references (`Vehicle[]`). A linear search traverses `v.getRegNo()`. Furthermore, OO allows trivial migration to an indexed `HashMap<String, Vehicle>`, reducing search time to $O(1)$.
  - Time Complexity: $O(n)$ in array, $O(1)$ in Hash map.

#### Adding a New Vehicle Attribute (e.g., `batteryCapacity` or `userAffiliation`):
- **Parallel Arrays:** Highly fragile and violates Open-Closed Principle. Requires allocating a whole new array `double[] batteryCapacity`, modifying every method signature that passes vehicle parameters, and risking desynchronization if one array is sorted or resized without updating the others.
- **Object-Oriented Model:** Cleanly encapsulated. Simply add a new field or create a specialized subclass (`ElectricCar extends Car`). Zero impact on existing working code.

#### Complexity & Memory Footprint Comparison Table:

| Metric | Parallel Arrays (`String[]`, `long[]`, `boolean[]`) | Object-Oriented Design (`Vehicle` Objects) |
| :--- | :--- | :--- |
| **Search by RegNo (Array)** | $O(n)$ time | $O(n)$ time |
| **Search by RegNo (Indexed Map)** | Complex parallel map coordination | $O(1)$ time via `HashMap<String, Vehicle>` |
| **Adding New Attribute** | Modifies all signatures, adds array: $O(\text{all code})$ | Add field/subclass: $O(1)$ change |
| **Space per Vehicle** | 3 reference pointers + 1 primitive = $\approx 24$ bytes | Object header (16B) + field refs (16B) $\approx 32$–$40$ bytes |
| **Data Integrity** | High risk of index mismatch / corruption | **Guaranteed** atomic encapsulation |

---

## 7. Execution and Verification
To compile and execute the Task 1 demonstration:
```bash
javac Task01/src/ProblemAnalysisDemo.java
java -cp Task01/src ProblemAnalysisDemo
```
Refer to `output.txt` for the recorded terminal run.
