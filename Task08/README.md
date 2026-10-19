# Task 8 – Apply Polymorphism
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4 Marks  

---

## 1. Compile-Time vs Runtime Polymorphism

### 1. Compile-Time (Static) Polymorphism: Method Overloading
Method overloading allows multiple methods to share the same name with distinct parameter lists.
- Overload 1: `public double calculateFee(int minutes)`
- Overload 2: `public double calculateFee(int minutes, double discountPercent)`
- **Compiler Selection:** The compiler resolves the target method at compile time based strictly on the number, order, and static types of the passed arguments.

### 2. Runtime (Dynamic) Polymorphism: Dynamic Method Dispatch
- **Upcasting:** A reference of superclass type `Vehicle` points to instances of any concrete subtype:
  ```java
  Vehicle[] parked = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };
  ```
- **Dynamic Method Dispatch:** When invoking `v.calculateFee(130)` in a loop, the Java Virtual Machine examines the actual runtime object on the heap and dispatches to the correct subtype implementation via the object's virtual method table (`vtable`).

---

## 2. Dynamic Dispatch Trace

### Tracing Call: `v.calculateFee(130)` for `ElectricCar("TN03C3")`
1. **Declared Reference Type:** `Vehicle`
2. **Actual Object Type on Heap:** `ElectricCar` (which extends `Car`, which extends `Vehicle`)
3. **Bytecode Instruction:** `invokevirtual #Method Vehicle.calculateFee:(I)D`
4. **JVM Resolution Step:**
   - The JVM inspects the object header of the target instance at runtime.
   - Finds the `vtable` pointer for `ElectricCar`.
   - `ElectricCar` inherits `calculateFee(int minutes)` from `Vehicle`.
   - Inside `Vehicle.calculateFee(int minutes)`, the call `this.ratePerHour()` is encountered.
   - Because `this` is dynamically dispatched, the JVM looks up `ratePerHour()` in `ElectricCar`'s vtable.
   - `ElectricCar` inherits `ratePerHour()` from `Car`, returning `30.0`.
   - Billable hours: $(130 + 59) / 60 = 3$ hours.
   - Base Fee: $3 \times 30.0 = Rs\ 90.0$.
5. **Interface Check:**
   - `if (v instanceof Chargeable c) fee += c.chargingFee(10);`
   - Evaluates to `true`; adds $10 \times 12.0 = Rs\ 120.0$.
   - **Total Fee:** $90.0 + 120.0 = Rs\ 210.0$.

---

## 3. Analysis: Why `new Car("TN02B2").equals(new ElectricCar("TN02B2"))` Prints `true`

### Execution Mechanics
In `Vehicle`:
```java
@Override
public boolean equals(Object o) {
    return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
}
```
1. `o` is an instance of `ElectricCar`.
2. Because `ElectricCar extends Car` and `Car extends Vehicle`, `o instanceof Vehicle` evaluates to `true`.
3. Both objects possess the exact registration number `"TN02B2"`.
4. Therefore, `equals` returns `true`.

### Evaluation: Is this the Correct Behaviour for the Smart Parking System?
- **From a Physical Parking Management Perspective: YES, IT IS CORRECT.**
  In real-world traffic enforcement and campus management, a vehicle is uniquely identified by its statutory license registration plate. No two vehicles on campus can legitimately possess the same registration plate simultaneously.
- **Symmetry & Asymmetry Caveat:**
  Because `instanceof Vehicle` is used, symmetry holds across all subclasses of `Vehicle` (`car.equals(ev)` is `true`, and `ev.equals(car)` is `true`).
  However, if the system strictly required identical classification types (i.e., a regular car cannot impersonate an electric car in charging bays), the implementation would require:
  ```java
  if (this.getClass() != o.getClass()) return false;
  ```
  For core identity and anti-passback tracking, identity by `regNo` is the correct domain model.

---

## 4. Alternative Concept and Complexity Analysis

### Dynamic Method Dispatch vs `instanceof` Ladder with Explicit Casting

#### The `instanceof` Ladder Alternative:
```java
double fee = 0;
if (v instanceof TwoWheeler tw) {
    fee = tw.calculateFee(minutes);
} else if (v instanceof ElectricCar ec) {
    fee = ec.calculateFee(minutes) + ec.chargingFee(kwh);
} else if (v instanceof Car c) {
    fee = c.calculateFee(minutes);
}
```

#### Comparison of Architectural Metrics:

| Metric | Dynamic Method Dispatch (Polymorphic) | `instanceof` Ladder with Casting |
| :--- | :--- | :--- |
| **Time per Call ($k$ vehicle types)** | **$O(1)$ Constant Time** (Direct vtable offset lookup) | **$O(k)$ Linear Time** (Evaluates sequential `if-else` branches) |
| **Cyclomatic Complexity** | **1** (Linear, branches encapsulated inside objects) | **$k$** (High branch complexity in caller) |
| **Extensibility (Adding $(k+1)^{\text{th}}$ type)** | Zero changes to caller loop (OCP compliant) | Caller must be edited, retested, and recompiled |
| **Type Safety** | Enforced at compile time | Vulnerable to casting mistakes and ordering bugs |

---

## 5. Execution and Verification
To compile and execute Task 8:
```bash
javac Task08/src/FeeDemo.java
java -cp Task08/src FeeDemo
```

### Verified Terminal Output:
```text
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true
```
Strictly matches the assignment specification.
