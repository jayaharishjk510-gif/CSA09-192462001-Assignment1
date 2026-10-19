# Task 12 – Day-4 Design Challenge: Re-evaluate and Adapt the Design
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K5 – Evaluate  
**Marks:** 8 Marks  

---

## Part A: Impact Analysis (2 Marks)

### 1. Changed Requirements Summary
The university parking operations expanded with four major requirements:
1. **New Category 1 (E-Bikes):** Charged a flat fee (e.g., flat $Rs\ 15.0$) regardless of parking duration.
2. **New Category 2 (Delivery Vans):** Charged at double the car rate ($Rs\ 60.0/\text{hr}$) during peak hours (9:00 AM – 5:00 PM), and standard car rate ($Rs\ 30.0/\text{hr}$) during off-peak hours.
3. **New User Pass (Visitor One-Day Pass):** Visitors may purchase a single-day pass allowing unlimited exits on that calendar day without per-hour billing.
4. **Structural Expansion:** Facility scaled from 3 to 5 floors, with non-uniform (variable) slot counts on each floor.

### 2. Affected Components & Data Structures
- **Floor & Slot Representation:**
  - A fixed rectangular array `String[5][MAX_SLOTS]` would cause severe memory wastage due to non-uniform slots.
  - A **Jagged Array** (`ParkingSlot[5][]`) natively accommodates variable capacities per floor ($[20, 30, 40, 35, 25]$) without resizing overhead or padding null entries.
- **Pricing Logic:**
  - Originally, pricing logic was directly coupled to a simple `ratePerHour() * hours` formula.
  - E-Bikes require **flat duration-independent pricing**.
  - Delivery Vans require **time-of-day dynamic peak pricing**.

### 3. Which Classes Must Be Modified vs Extended?
- **Classes Requiring Modification:** **NONE.** Core classes (`Vehicle`, `Car`, `TwoWheeler`, `ElectricCar`) remain **100% untouched**.
- **Classes Extended / Added:**
  - New Subclasses: `EBike extends Vehicle`, `DeliveryVan extends Vehicle`.
  - New Strategy Abstraction: `PricingRule` interface with `FlatFeePricingRule` and `PeakHourPricingRule`.
  - New Pass Abstraction: `Pass` interface with `MonthlyPass` and `OneDayPass`.

---

## Part B: Evaluation of the Original Design (2 Marks)

| Architectural Dimension | Original Design Status | Evaluation Under New Requirements |
| :--- | :--- | :--- |
| **Adding New Vehicle Types** | Extensible via subclassing | **Fully Supported:** Adding `EBike` and `DeliveryVan` was achieved strictly by writing new subclasses without rewriting existing classes. |
| **Pricing Rules Organization** | Template method calling `ratePerHour()` | **Needed Decoupling:** While sufficient for linear tariffs, complex time-of-day dynamic tariffs benefit from Strategy-pattern pricing rules. |
| **Preservation of Tested Code** | High cohesion | **Zero Regression:** All existing tested classes (`Car`, `TwoWheeler`, `ElectricCar`) remain completely intact. |

**Verdict:** The original object-oriented model was sound in its inheritance and encapsulation boundaries. Complete redesign was **unnecessary**; the correct architectural response was **modular adaptation**.

---

## Part C: Adaptation Architecture (2 Marks)

### Revised Class Structure:
1. **Jagged Array for Floors:**
   ```java
   ParkingSlot[][] slots = new ParkingSlot[5][];
   slots[0] = new ParkingSlot[20];
   slots[1] = new ParkingSlot[30];
   slots[2] = new ParkingSlot[40];
   slots[3] = new ParkingSlot[35];
   slots[4] = new ParkingSlot[25];
   ```
2. **`PricingRule` Strategy Abstraction:**
   ```java
   public interface PricingRule {
       double computeFee(int minutes, LocalTime entryTime, LocalTime exitTime);
   }
   ```
3. **`Pass` Abstraction:**
   ```java
   public interface Pass {
       double applyPassWaiver(double calculatedBaseFee);
       boolean isValidToday();
   }
   ```
4. **Concrete Implementations:**
   - `EBike`: Uses `FlatFeePricingRule(15.0)`.
   - `DeliveryVan`: Uses `PeakHourPricingRule(30.0, 2.0)` checking if stay intersects 09:00–17:00.
   - `OneDayPass`: Implements `Pass`, waives 100% of base parking tariff for valid date.

---

## Part D: Evidence-Based Defence (2 Marks)

### 1. Concrete Quantitative Metrics:
- **Number of Existing Working Classes Modified:** **0 classes** (Vehicle, Car, TwoWheeler untouched).
- **Number of New Classes Introduced:** 8 modular classes/interfaces.
- **Open-Closed Principle (OCP):** **100% Compliant.** Closed for modification, open for extension.

### 2. Empirical Verification Results:
```text
Extended 5-Floor Capacities:
  Floor 0: 20 slots | Floor 1: 30 slots | Floor 2: 40 slots | Floor 3: 35 slots | Floor 4: 25 slots
Total Facility Capacity: 150 slots

New Category 1: E-Bike (Flat Fee Rule):
  Short Stay (30 mins):  Net Fee = Rs 15.0
  Long Stay (300 mins):  Net Fee = Rs 15.0
  -> Result: Identical flat fee verified.

New Category 2: Delivery Van (Dynamic Peak Pricing):
  Peak Stay (10:00 - 12:00, 2 hrs @ Rs 60/hr):  Net Fee = Rs 120.0
  Off-Peak Stay (19:00 - 21:00, 2 hrs @ Rs 30/hr): Net Fee = Rs 60.0
  -> Result: Double peak rate verified.

Visitor One-Day Pass:
  With One-Day Pass: Base Rs 120.0 | Waiver: -Rs 120.0 | NET: Rs 0.0
  Without Pass:      Base Rs 120.0 | Waiver: -Rs 0.0   | NET: Rs 120.0
  -> Result: Full day waiver verified.
```

### 3. Ease of Future Expansion:
If the university later introduces autonomous delivery bots or weekend holiday surge pricing, developers can simply implement a new `PricingRule` without touching any vehicle class.

---

## 5. Execution and Verification
To compile and execute Task 12:
```bash
javac Task12/src/parking/rule/*.java Task12/src/parking/model/*.java Task12/src/parking/service/*.java Task12/src/parking/app/*.java
java -cp Task12/src parking.app.DesignChallengeVerification
```
Refer to `output.txt` for recorded verification.
