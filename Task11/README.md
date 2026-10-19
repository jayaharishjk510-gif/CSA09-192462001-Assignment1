# Task 11 – Implement, Test and Defend the Design
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K5 – Evaluate  
**Marks:** 5 Marks  

---

## 1. Test Verification Matrix (Expected vs Actual)

| Test ID | Test Category | Vehicle & Scenario | Input Parameters | Expected Result | Actual Result | Status |
| :---: | :--- | :--- | :--- | :--- | :--- | :---: |
| **TC-01** | Normal | Two-Wheeler Visitor | `TN01N101`, 90 min | 2 hrs @ Rs 10 = Rs 20.0 | Rs 20.0 | **PASS** |
| **TC-02** | Normal | Car Student Pass | `TN02N202`, 90 min | 2 hrs @ Rs 30 = Rs 0.0 (Waived) | Rs 0.0 | **PASS** |
| **TC-03** | Normal | EV Staff Pass + Charging | `TN03N303`, 90 min, 0 kWh | Base waived = Rs 0.0 | Rs 0.0 | **PASS** |
| **TC-04** | Boundary | Fill to Last Slot | 3 slots capacity | Ticket issued at last free bay | `Pos=(1,0)` Allocated | **PASS** |
| **TC-05** | Boundary | Facility Overflow | Attempt 4th vehicle | `null` returned | `null` | **PASS** |
| **TC-06** | Boundary | Zero-Minute Stay | `TN11ZERO`, 0 min | 0 billable hours = Rs 0.0 | Rs 0.0 | **PASS** |
| **TC-07** | Boundary | Exact 1-Hour Boundary | `TN11SLAB60`, 60 min | 1 hr @ Rs 30 = Rs 30.0 | Rs 30.0 | **PASS** |
| **TC-08** | Boundary | 1-Minute Past Boundary | `TN11SLAB61`, 61 min | 2 hrs @ Rs 30 = Rs 60.0 | Rs 60.0 | **PASS** |
| **TC-09** | Invalid | Null Vehicle | `null` | Throws `IllegalArgumentException` | Exception Caught | **PASS** |
| **TC-10** | Invalid | Duplicate License Plate | `TN09DUP111` already in | Throws `IllegalStateException` | Exception Caught | **PASS** |
| **TC-11** | Invalid | Vacate Ghost Vehicle | `TN99GHOST` | Throws `NoSuchElementException` | Exception Caught | **PASS** |

---

## 2. Hand-Calculated Daily Scenario Reconciliation

### Scenario Specifications:
A test day with 6 vehicles of varying categories, affiliations, and durations:
1. `TN01AA1001` (TwoWheeler, Visitor): Parked 45 min $\implies$ Ceiling = 1 hr @ Rs 10 = **Rs 10.0**
2. `TN01AA1002` (TwoWheeler, Student): Parked 180 min $\implies$ Ceiling = 3 hrs @ Rs 10 = Rs 30.0; Waived by Monthly Pass = **Rs 0.0**
3. `TN02BB2001` (Car, Visitor): Parked 60 min $\implies$ Ceiling = 1 hr @ Rs 30 = **Rs 30.0**
4. `TN02BB2002` (Car, Visitor): Parked 61 min $\implies$ Ceiling = 2 hrs @ Rs 30 = **Rs 60.0**
5. `TN03CC3001` (ElectricCar, Staff): Parked 120 min (Base Rs 60 waived) + 15 kWh charging @ Rs 12 = **Rs 180.0**
6. `TN03CC3002` (ElectricCar, Visitor): Parked 130 min (3 hrs @ Rs 30 = Rs 90) + 20 kWh charging @ Rs 12 = **Rs 330.0**

### Reconciliation Mathematical Proof:
$$\text{Total Daily Revenue} = 10.0 + 0.0 + 30.0 + 60.0 + 180.0 + 330.0 = \mathbf{Rs\ 610.00}$$

### Programmatic Verification Output:
```text
  Invoice #1008 | TN01AA1001 (TwoWheeler, VISITOR) | 45 mins (1 hrs @ Rs 10) | Base: Rs 10.0 | EV: Rs 0.0 | Waiver: -Rs 0.0 | NET: Rs 10.0
  Invoice #1009 | TN01AA1002 (TwoWheeler, STUDENT) | 180 mins (3 hrs @ Rs 10) | Base: Rs 30.0 | EV: Rs 0.0 | Waiver: -Rs 30.0 | NET: Rs 0.0
  Invoice #1010 | TN02BB2001 (Car, VISITOR) | 60 mins (1 hrs @ Rs 30) | Base: Rs 30.0 | EV: Rs 0.0 | Waiver: -Rs 0.0 | NET: Rs 30.0
  Invoice #1011 | TN02BB2002 (Car, VISITOR) | 61 mins (2 hrs @ Rs 30) | Base: Rs 60.0 | EV: Rs 0.0 | Waiver: -Rs 0.0 | NET: Rs 60.0
  Invoice #1012 | TN03CC3001 (ElectricCar, STAFF) | 120 mins (2 hrs @ Rs 30) | Base: Rs 60.0 | EV: Rs 180.0 | Waiver: -Rs 60.0 | NET: Rs 180.0
  Invoice #1013 | TN03CC3002 (ElectricCar, VISITOR) | 130 mins (3 hrs @ Rs 30) | Base: Rs 90.0 | EV: Rs 240.0 | Waiver: -Rs 0.0 | NET: Rs 330.0

Hand-Calculated Total Expected: Rs 610.00
Program Computed Actual Total:  Rs 610.00
Reconciliation Verdict: EXACT MATCH (100% RECONCILED)
```

---

## 3. Evaluation of Object-Oriented Principles & System Defence

1. **Encapsulation:**
   - All internal bay matrices and ticket sequences are private. Mutation occurs solely through validated methods (`parkVehicle`, `vacateVehicle`), preventing illegal states like double-occupancy or negative tariffs.
2. **Inheritance:**
   - Eliminates redundant license and duration logic via `Vehicle` $\longrightarrow$ `Car` $\longrightarrow$ `ElectricCar`.
3. **Polymorphism:**
   - Runtime method dispatch dynamically selects appropriate hourly rates through `v.calculateFee(minutes)` without fragile `instanceof` inspection.
4. **Abstraction:**
   - `Chargeable` decouples EV electrical capabilities from internal vehicle combustion mechanics, allowing easy extension to novel electric transport.

### Identified System Limitation:
- **Coarse Synchronization Bottleneck:** Current allocation methods use coarse method-level `synchronized` locks on `ParkingFacility`. While fully thread-safe for correctness, under high concurrent load across 10 gate entrances, entrance threads experience lock contention.
- **Remediation:** Introduce fine-grained per-floor lock striping (`ReentrantLock[] floorLocks`) or lock-free concurrent queues.

---

## 4. Alternative Concept and Complexity Analysis

### Comparison: Active Tickets Stored in Array vs `ArrayList` vs `HashMap<String, ParkingTicket>`

When a vehicle exits, the attendant scans the license plate (`regNo`) to retrieve the entry ticket.

#### Empirical Benchmark Measurements ($n = 1000$ active vehicles):
- **Array Linear Scan ($O(n)$):** $130,500\text{ ns}$
- **ArrayList Linear Scan ($O(n)$):** $226,200\text{ ns}$
- **HashMap Indexed Lookup ($O(1)$):** **$13,200\text{ ns}$ ($\approx 9.9\times$ faster!)**

| Structure | Time to Find Ticket on Exit | Time to Insert Ticket | Space Complexity | Dynamic Resizing |
| :--- | :--- | :--- | :--- | :--- |
| **Fixed Array `ticketArray[]`** | $O(n)$ worst-case scan | $O(1)$ at next index | $O(N)$ capacity | Rigid; fixed upfront size |
| **`ArrayList<ParkingTicket>`** | $O(n)$ worst-case scan | $O(1)$ amortized append | $O(n)$ capacity | Dynamic growth |
| **`HashMap<String, Ticket>`** | **$O(1)$ amortized lookup** | **$O(1)$ amortized insert** | $O(n)$ with bucket overhead | Dynamic rehashing |

**Recommendation:** For production campus parking systems with thousands of vehicles, active tickets must be indexed in a `ConcurrentHashMap<String, ParkingTicket>` keyed by vehicle registration number.

---

## 5. Execution and Verification
To compile and execute Task 11:
```bash
javac Task11/src/parking/model/*.java Task11/src/parking/service/*.java Task11/src/parking/test/*.java
java -cp Task11/src parking.test.SmartParkingSystemTest
```
Refer to `output.txt` for recorded verification.
