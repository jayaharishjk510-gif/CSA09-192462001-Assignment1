# CSA09-192462001-Assignment1
## Design, Implement and Validate an Object-Oriented Model for a Campus Smart-Parking System

**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming  
**Course Outcome:** CO1 – Java fundamentals and object-oriented programming  
**Nature of Assignment:** Individual  
**Total Marks:** 50  

---

### Student Information
- **Student Name:** Jaya Harish J
- **Register Number:** 192462001
- **Java Version:** OpenJDK 21.0.12.1 LTS (Microsoft Build with HotSpot)
- **Operating System:** Windows 11 x64
- **Repository Name:** `CSA09-192462001-Assignment1`
- **GitHub Repository Link:** `https://github.com/jayaharish/CSA09-192462001-Assignment1`

---

## Repository Structure
Each task is organized in its own self-contained folder containing Java source code (`src/`), execution inputs (`input.txt`), verified terminal outputs (`output.txt`), and task analysis documentation (`README.md`):

```text
CSA09-192462001-Assignment1/
├── Task01/   # Problem Analysis & Candidate Objects (OO vs Parallel Arrays)
├── Task02/   # Data Representation & Precision Loss (Jagged vs 1D Arrays)
├── Task03/   # Control Logic & SIMATS Pseudocode (ParkingFacility.java)
├── Task04/   # Classes, Constructors & Memory Layout (ParkingTicket)
├── Task05/   # static Members & Object Life Cycle (Multi-Gate Analysis)
├── Task06/   # Inheritance Hierarchy & UML Diagram (Vehicle -> Car -> ElectricCar)
├── Task07/   # Method Overriding, final & Object Contract (equals/hashCode)
├── Task08/   # Polymorphism & Dynamic Dispatch (FeeDemo.java)
├── Task09/   # Abstraction (Abstract Classes vs Interfaces: Chargeable, Payable)
├── Task10/   # Packages & Encapsulation (parking.model, service, app)
├── Task11/   # Full Implementation, Testing & Defence (Rs 610 Reconciled Scenario)
├── Task12/   # Day-4 Design Challenge: E-Bikes, Delivery Vans & One-Day Pass
├── ASSIGNMENT_REPORT.md # Comprehensive 50-Mark Assignment Report
└── README.md            # Root Project Documentation and Execution Guide
```

---

## Compilation and Execution Guide

### Prerequisites
Ensure Java 17+ or Java 21 is configured on your system:
```powershell
javac -version
java -version
```

### Steps to Compile and Run Each Task

#### Task 1: Problem Analysis and Candidate Objects
```powershell
javac Task01\src\ProblemAnalysisDemo.java
java -cp Task01\src ProblemAnalysisDemo
```
- **Result:** Successfully demonstrates that OO encapsulation guarantees atomic data integrity over fragile parallel arrays and verifies search complexity.

#### Task 2: Data Representation and Precision Loss
```powershell
javac Task02\src\DataRepresentationDemo.java
java -cp Task02\src DataRepresentationDemo
```
- **Result:** Proves how integer truncation `minutes / 60` leads to lost parking revenue, and validates the correct ceiling formula `(minutes + 59) / 60`.

#### Task 3: Control Logic & Complete the Code (`ParkingFacility.java`)
```powershell
javac Task03\src\ParkingFacility.java Task03\src\OptimizedParkingFacility.java
java -cp Task03\src ParkingFacility
java -cp Task03\src OptimizedParkingFacility
```
- **Result:** Output matches the university expected output byte-for-byte:
  ```text
  [0, 0]
  [0, 1]
  [1, 0]
  null
  true false false
  [0, 1]
  ```

#### Task 4: Classes, Constructors and Memory Layout
```powershell
javac Task04\src\ConstructorChainingDemo.java
java -cp Task04\src ConstructorChainingDemo
```
- **Result:** Demonstrates canonical constructor chaining via `this(...)`, defensive duplicate ticket creation via copy constructor, and JVM Stack vs Heap layout.

#### Task 5: static Members and Object Life Cycle
```powershell
javac Task05\src\StaticLifecycleDemo.java
java -cp Task05\src StaticLifecycleDemo
```
- **Result:** Proves that static ticket sequence counters prevent ticket ID collision across multiple gates (North Gate vs South Gate), demonstrates pass-by-value of references, and verifies GC eligibility.

#### Task 6: Inheritance Hierarchy and UML
```powershell
javac Task06\src\InheritanceHierarchyDemo.java
java -cp Task06\src InheritanceHierarchyDemo
```
- **Result:** Traces exact constructor invocation order (`Vehicle` $\rightarrow$ `Car` $\rightarrow$ `ElectricCar`) and verifies UML class relationships.

#### Task 7: Method Overriding, final & Object Contract
```powershell
javac Task07\src\MethodOverridingDemo.java
java -cp Task07\src MethodOverridingDemo
```
- **Result:** Enforces the `equals()` and `hashCode()` contract based on vehicle registration plate, successfully deduplicating parked vehicles inside a `HashSet`.

#### Task 8: Polymorphism & Complete the Code (`FeeDemo.java`)
```powershell
javac Task08\src\FeeDemo.java
java -cp Task08\src FeeDemo
```
- **Result:** Dynamic method dispatch executes correctly and produces the exact required output:
  ```text
  TwoWheeler TN01A1 -> Rs 30.0
  Car TN02B2 -> Rs 90.0
  ElectricCar TN03C3 -> Rs 210.0
  true
  ```

#### Task 9: Abstraction (Abstract Classes vs Interfaces)
```powershell
javac Task09\src\AbstractionDemo.java
java -cp Task09\src AbstractionDemo
```
- **Result:** Demonstrates how `ElectricCar` obtains multiple behaviors by extending `Car` and implementing `Chargeable` and `Trackable`, while proving why `Vehicle` cannot be an interface.

#### Task 10: Packages and Encapsulation
```powershell
javac Task10\src\parking\model\*.java Task10\src\parking\service\*.java Task10\src\parking\app\*.java
java -cp Task10\src parking.app.EncapsulationDemo
```
- **Result:** Validates 3-tier package isolation (`parking.model`, `service`, `app`), exposes the Integer Cache `==` trap outside $[-128, 127]$, and verifies defensive validation guards.

#### Task 11: Implement, Test and Defend the Design
```powershell
javac Task11\src\parking\model\*.java Task11\src\parking\service\*.java Task11\src\parking\test\*.java
java -cp Task11\src parking.test.SmartParkingSystemTest
```
- **Result:** All 11 automated test cases pass (Normal, Boundary, Invalid). Hand-calculated daily revenue scenario reconciles 100% to **Rs 610.00**. HashMap exit lookup is **9.9x faster** than linear array scanning.

#### Task 12: Day-4 Design Challenge (Adaptive Extension)
```powershell
javac Task12\src\parking\rule\*.java Task12\src\parking\model\*.java Task12\src\parking\service\*.java Task12\src\parking\app\*.java
java -cp Task12\src parking.app.DesignChallengeVerification
```
- **Result:** Extended 5-floor jagged facility (150 slots total), verified E-Bike flat fee of Rs 15.0, verified Delivery Van peak pricing (Rs 60/hr peak vs Rs 30/hr off-peak), and validated Visitor One-Day Pass waivers with **zero modifications to existing working classes**.

---

## Progressive Commit History
Progressive commits have been maintained throughout the development lifecycle:
- `5cce8db` - Task 1: Complete problem analysis, object identification, and parallel array complexity comparison
- `c5a8a18` - Task 2: Choose data representation, resolve casting precision loss, and evaluate jagged vs 1D array models
- `c5f7f49` - Task 3: Implement control logic, complete ParkingFacility TODOs, provide SIMATS pseudocode, and evaluate O(1) free stack alternative
- `0045317` - Task 4: Design classes, constructors, chaining, copy constructor, and analyze stack vs heap memory layout
- `75f30f4` - Task 5: Apply static members, explain object lifecycle and GC, and evaluate multi-gate ticket sequence collision
- `19758f3` - Task 6: Design inheritance hierarchy, trace constructor order, generate UML diagrams, and compare with composition
- `262b0ad` - Task 7: Method overriding, enforce final constraints, implement equals/hashCode contract, and analyze switch vs polymorphism
- `5ced943` - Task 8: Complete FeeDemo TODOs, explain polymorphism dispatch trace, evaluate equals contract, and analyze instanceof complexity
- `a0fdaa3` - Task 9: Introduce abstraction via abstract classes and interfaces, demonstrate multiple behaviors, and analyze inversion limits
- `d004724` - Task 10: Enforce encapsulation through 3-tier packages, access modifiers, defensive guards, and analyze autoboxing pitfalls
- `5b042ac` - Task 11: Implement complete system test suite, reconcile Rs 610 hand-calculated daily scenario, defend design, and benchmark exit lookups
- `d172083` - Task 12: Day-4 Design Challenge - Impact analysis, modular pricing rule adaptation, jagged 5-floor facility, and evidence defence
