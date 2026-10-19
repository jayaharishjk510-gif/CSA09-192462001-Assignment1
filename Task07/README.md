# Task 7 – Override and Specialise Behaviour
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4 Marks  

---

## 1. Method Overriding Principles and Rules

### 1. Overridden Methods
- **`protected abstract double ratePerHour()`**:
  - Defined abstractly in `Vehicle`. Subclasses (`TwoWheeler`, `Car`, `ElectricCar`) override this method to supply their category-specific hourly tariff ($Rs\ 10.0$, $Rs\ 30.0$).
- **`toString()`, `equals(Object o)`, `hashCode()`**:
  - Overridden from `java.lang.Object` to establish logical identity and string representation.

### 2. The `@Override` Annotation
- **Purpose:** Instructs the Java compiler to verify that a method signature exactly matches an inherited method from the superclass or interface.
- **Fail-Safe Benefit:** If a developer makes a typographical error (e.g., typing `rateperhour()` or changing parameter types to `long minutes`), the compiler immediately halts compilation with an error, preventing subtle runtime bugs.

### 3. Rules of Method Overriding in Java
1. **Method Signature:** Method name and parameter list must be identical.
2. **Return Type:** Must be identical or **covariant** (i.e., a subtype of the superclass method return type).
3. **Access Modifiers:** The overriding method cannot assign more restrictive access than the superclass method:
   - Superclass `protected` $\longrightarrow$ Subclass `protected` or `public` (Permitted).
   - Superclass `public` $\longrightarrow$ Subclass `private` or `protected` (**Forbidden** by compiler).
4. **Exceptions:** Cannot throw broader or new checked exceptions than declared in superclass.

---

## 2. Strategic Use of the `final` Keyword

| Level | Usage in Parking System | Architectural Justification |
| :--- | :--- | :--- |
| **`final` Variable** | `private final String regNo;` | Enforces state immutability. A vehicle’s registration plate cannot be mutated after creation. |
| **`final` Method** | `public final double calculateFee(...)` | Implements the **Template Method Pattern**. Guarantees that no subclass can tamper with the fundamental ceiling-billing algorithm (`(minutes + 59) / 60 * ratePerHour()`). |
| **`final` Class** | `public final class ElectricCar` | Prevents further subclassing where specialization is complete, protecting domain boundaries and assisting JVM devirtualization optimizations. |

---

## 3. Object-Class Overrides: `equals()`, `hashCode()`, and `toString()`

### 1. Business Requirement: Identity by Registration Number
In the physical world, two vehicles with the license plate `"TN01AA1111"` represent the same physical entity, even if represented by different Java object instances in memory:
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Vehicle other)) return false;
    return Objects.equals(this.regNo, other.regNo);
}

@Override
public int hashCode() {
    return Objects.hash(regNo);
}
```

### 2. Contract Adherence
- **Contract Rule:** If `v1.equals(v2)` is `true`, then `v1.hashCode()` **must** equal `v2.hashCode()`.
- **System Consequence:** If `hashCode()` were omitted, a `HashSet<Vehicle>` or `HashMap<Vehicle, Ticket>` would compute bucket indices using system memory addresses, allowing duplicate vehicles to be admitted to the parking facility simultaneously!

---

## 4. Alternative Concept and Complexity Analysis

### Comparison: Subclass Overriding vs Switch Statement on Type Enum

#### Flawed Switch Approach:
```java
public double calculateFee(VehicleTypeEnum type, int minutes) {
    int hours = (minutes + 59) / 60;
    return switch(type) {
        case TWO_WHEELER -> hours * 10.0;
        case CAR -> hours * 30.0;
        case ELECTRIC_CAR -> hours * 30.0;
    };
}
```

#### Analytical Breakdown:
1. **Open-Closed Principle (OCP) Violation:**
   - With `switch`, introducing a new category (e.g., `EBike`, `DeliveryVan`) forces modification of the enum, the calculator class, and every other method with a similar switch ladder across the entire enterprise system.
   - With overriding, simply declare `class EBike extends Vehicle`. Zero existing classes are modified!
2. **Number of Classes Modified:**
   - **Overriding:** $0$ existing classes changed ($1$ new class created).
   - **Switch:** Multiple existing core service classes modified and recompiled.
3. **Execution Time for $k$ Vehicle Types:**
   - **Overriding:** $O(1)$ constant time lookup via the virtual method table (`vtable`).
   - **Switch:** $O(1)$ table-switch or $O(k)$ lookup-switch depending on bytecode compiler branch distribution.

| Metric | Polymorphic Method Overriding | Single Switch on Type Field |
| :--- | :--- | :--- |
| **Open-Closed Principle** | **Strictly Upheld** | **Violated** (Modifies existing code) |
| **Time Complexity** | $O(1)$ vtable dispatch | $O(1)$ to $O(k)$ branching |
| **Classes Modified on New Type** | **0 classes modified** | Enum + all switch classes ($N$ classes) |
| **Coupling** | Loosely coupled | Highly coupled |

---

## 5. Execution and Verification
To compile and execute Task 7:
```bash
javac Task07/src/MethodOverridingDemo.java
java -cp Task07/src MethodOverridingDemo
```
Refer to `output.txt` for recorded verification.
