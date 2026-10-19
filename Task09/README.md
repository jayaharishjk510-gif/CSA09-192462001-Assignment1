# Task 9 – Introduce Abstraction
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4 Marks  

---

## 1. Abstract Classes vs Interfaces in System Design

### 1. Abstract Class Selection: `Vehicle`
- **Why Abstract Class:** `Vehicle` represents a fundamental domain entity that models physical state:
  - Maintains private state: `private final String regNo`.
  - Enforces invariant validation via constructor: `protected Vehicle(String regNo)`.
  - Provides concrete shared template logic: `calculateFee(int minutes)` with ceiling arithmetic.
  - Declares abstract hook: `protected abstract double ratePerHour()`.
- **Rule of Thumb:** Use an **abstract class** when classes share an **IS-A identity**, common state (fields), and shared code implementation.

### 2. Interface Selection: `Chargeable`, `Payable`, `Trackable`
- **Why Interfaces:** Model peripheral capabilities and cross-cutting behaviors:
  - `Chargeable`: Charging is not an inherent trait of all vehicles, but a capability supported by specific vehicles. Tomorrow, electric campus lawnmowers, security golf carts, or delivery drones could implement `Chargeable`.
  - `Payable`: Can be implemented by drivers, passes, or external corporate sponsor accounts.
  - `Trackable`: IoT telemetry contract.
- **Rule of Thumb:** Use an **interface** to establish a **CAN-DO capability** contract across unrelated class hierarchies.

---

## 2. Multiple Behaviors and Interface Evolution

### 1. Multiple Inheritance of Type
Java disallows multiple class inheritance (`extends A, B` is illegal) to eliminate the Deadly Diamond of Death.
However, Java supports unlimited interface implementation:
```java
public class ElectricCar extends Car implements Chargeable, Trackable { ... }
```
An `ElectricCar` instance can be treated as a `Vehicle`, a `Car`, a `Chargeable` device, and a `Trackable` IoT asset.

### 2. Default and Static Methods in Interfaces
- **Default Method (`default double getEcoDiscountPercentage()`):**
  Allows interface designers to add new capabilities to existing interfaces with a default implementation without breaking existing classes that implement the interface.
- **Static Method (`static double getStandardUnitTariff()`):**
  Houses cohesive utility functions directly within the interface namespace.

---

## 3. Alternative Concept and Complexity Analysis

### Architectural Inversion: `Vehicle` as Interface & `Chargeable` as Abstract Class

Suppose the design is inverted:
1. `Vehicle` is declared as an `interface` with default methods.
2. `Chargeable` is declared as an `abstract class`.

#### What Breaks and Can No Longer Be Expressed:
1. **Loss of Instance State and Encapsulation:**
   - Interfaces **cannot declare instance variables**. All fields in an interface are implicitly `public static final` (constants).
   - Therefore, `regNo` cannot reside in `Vehicle`. Every single vehicle subclass (`TwoWheeler`, `Car`, `Bus`, `Van`) would be forced to duplicate `private String regNo;`, getters, setters, and validations. Code duplication explodes!
2. **Loss of Constructor Invariants:**
   - Interfaces cannot define constructors. The system cannot guarantee that a `Vehicle` is always instantiated with a non-null, valid registration plate at the base layer.
3. **Multiple Inheritance Collision:**
   - If `Chargeable` is an abstract class, then `ElectricCar` **cannot extend both `Car` and `Chargeable`**! Java allows only one superclass (`extends Car`). An electric car would be completely locked out from sharing the car hierarchy, requiring fragile workarounds.

#### Method Dispatch and Memory Footprint Comparison:

| Dimension | Standard Design (`Vehicle` Abstract Class) | Inverted Design (`Vehicle` Interface) |
| :--- | :--- | :--- |
| **Bytecode Instruction** | `invokevirtual` | `invokeinterface` |
| **Method Dispatch Time** | $O(1)$ fast indexed vtable lookup | $O(1)$ itable lookup (slightly higher micro-overhead) |
| **Object State Storage** | Centralized in superclass ($1$ location) | Duplicated across all leaf classes |
| **Subclass Duplication** | $0\%$ field duplication | **$100\%$ duplicated state boilerplate** |
| **Cross-Cutting Extensibility** | High (interfaces attached freely) | Severely restricted by single inheritance |

---

## 4. Execution and Verification
To compile and execute Task 9:
```bash
javac Task09/src/AbstractionDemo.java
java -cp Task09/src AbstractionDemo
```
Refer to `output.txt` for recorded verification.
