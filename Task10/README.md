# Task 10 – Enforce Encapsulation through Packages and Access Modifiers
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 3 Marks  

---

## 1. Package Structure and Architectural Layering

The system is organized into a clean 3-tier package architecture:
```text
parking/
├── model/       # Domain Entities & Value Objects (Vehicle, Car, TwoWheeler, ElectricCar, ParkingSlot, ParkingTicket)
├── service/     # Business Logic & Orchestrators (ParkingFacility, FeeCalculator)
└── app/         # Application Presentation & Verification Entry Points (EncapsulationDemo)
```

---

## 2. Access Modifier Strategy and Field Protection

| Access Level | Target Member | Architectural Justification |
| :--- | :--- | :--- |
| **`private`** | `Vehicle.regNo`, `ParkingSlot.parkedVehicle`, `ParkingFacility.slots` | Prevents external modification. Internal data representations cannot be corrupted or directly reassigned by outside callers. |
| **`default` (package-private)** | Internal slot state synchronizers | Permits trusted collaboration between service components in the same package while remaining hidden from external applications. |
| **`protected`** | `Vehicle.ratePerHour()`, constructors | Restricts access to subclasses and same-package classes, enabling controlled inheritance specialization. |
| **`public`** | `allocate()`, `release()`, `calculateFee()` | Exposes clean, stable, and validated public APIs to calling applications. |

---

## 3. Data Validation Guards

Defensive programming invariants are enforced at the point of mutation:
1. **Registration Plate Validation:** Rejects `null` or whitespace-only plates via `IllegalArgumentException`.
2. **Negative Duration Guard:** `calculateFee(int minutes)` throws an exception if `minutes < 0`.
3. **Slot Occupancy Guard:** `ParkingSlot.assignVehicle(Vehicle v)` verifies that the slot is vacant before assigning; throws `IllegalStateException` on double-parking attempts.
4. **Boundary Guard:** `vacateSlot(int floor, int slot)` validates that floor and slot indices are non-negative and strictly within bounds.

---

## 4. Wrapper Classes and Autoboxing Hazards

### 1. The Integer Cache Identity Trap (`==` vs `.equals()`)
- Java caches `Integer` objects in the range $[-128, 127]$.
- For values within this range:
  ```java
  Integer a = 100, b = 100;
  a == b; // Evaluates to TRUE (references the same cached Flyweight instance)
  ```
- For values outside this range:
  ```java
  Integer x = 1000, y = 1000;
  x == y; // Evaluates to FALSE! (Two distinct heap allocations)
  x.equals(y); // Evaluates to TRUE (Safe value comparison)
  ```
- **Hazard in Parking System:** Comparing ticket IDs or vehicle counts using `==` will succeed in unit tests with small numbers ($<128$), but silently corrupt production billing once ticket counts exceed 127!

### 2. NullPointerException During Autounboxing
- When converting a boxed wrapper (`Long`, `Integer`, `Double`) to a primitive (`long`, `int`, `double`):
  ```java
  Long boxedTicketId = null;
  long id = boxedTicketId; // Throws NullPointerException at runtime!
  ```
- The design guards against null unboxing by validating wrappers at the entry boundary.

---

## 5. Alternative Concept and Complexity Analysis

### Comparison: Private Encapsulated Validation vs Public Fields with Caller Validation

#### The Flawed Public Fields Approach:
```java
public class VulnerableSlot {
    public int floor;
    public int slot;
    public String vehicleNo;
}

// Caller must remember to validate every time:
if (floor >= 0 && floor < maxFloors && slot >= 0 && slot < maxSlots) {
    s.vehicleNo = "TN01AA1111";
}
```

#### Analytical Breakdown:
1. **Repetition and Bug Surface Area:**
   - With public fields, the exact same validation check must be copy-pasted across every entrance barrier, mobile app handler, web portal, and test script.
   - If a new business rule is added (e.g., verifying vehicle emission category), all $N$ calling locations across the enterprise must be manually tracked down and edited. If even one programmer forgets the check, the facility state is corrupted.
2. **Encapsulated Mutators:**
   - Centralizes validation in **exactly 1 location** (`assignVehicle`).
3. **Execution Overhead:**
   - Validation inside a setter takes $O(1)$ constant time ($\approx 1$–$3$ nanoseconds for bounds check), while preventing multi-million rupee billing corruption and system crashes.

| Metric | Encapsulated Private Fields + Setters | Public Fields + Caller-Side Validation |
| :--- | :--- | :--- |
| **Validation Locations** | **1 single centralized method** | $N$ duplicate caller sites across system |
| **Risk of Corrupt State** | **0% (Protected invariants)** | **High** (Caller omission corrupts data) |
| **Maintenance on Rule Change** | Modify 1 setter method | Modify all $N$ calling scripts |
| **Time per Update** | $O(1)$ ($\approx 2$ ns) | $O(1)$ ($\approx 2$ ns, but brittle) |
| **Data Security & Audit** | Complete control via access modifiers | Unrestricted tampering |

---

## 6. Execution and Verification
To compile and execute Task 10:
```bash
javac Task10/src/parking/model/*.java Task10/src/parking/service/*.java Task10/src/parking/app/*.java
java -cp Task10/src parking.app.EncapsulationDemo
```
Refer to `output.txt` for recorded verification.
