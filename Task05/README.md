# Task 5 – Apply static Members and the Object Life Cycle
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 3 Marks  

---

## 1. Static Members Identification and Justification

### 1. Static Variables
- **Global Ticket Sequence Counter (`globalSequenceCounter`):**
  - *Why Static:* Belongs to the class as a whole and resides in JVM Metaspace. Guarantees that every newly issued ticket receives a strictly increasing, globally unique sequence number across the entire campus.
  - *Why Instance Variable Fails:* If declared as an instance variable of `ParkingTicket`, every new ticket would initialize its counter to 1, causing immediate ticket collisions.
- **Global Campus Occupancy (`activeVehiclesOnCampus`):**
  - Tracks the aggregate count of occupied bays across all levels and gates. Static state enables global capacity reporting without traversing all floor arrays.

### 2. Static Methods and Static Initializer Block
- **Static Initializer Block (`static { ... }`):**
  - Executes once when the class is loaded into memory by the JVM ClassLoader. Used to configure campus billing constants, load tax rates, or initialize hardware gate controller drivers.
- **Static Factory / Utility Methods (`CampusTicket.getActiveCampusOccupancy()`):**
  - Accessed directly via the class name without requiring instance creation.

---

## 2. Object Life Cycle and Garbage Collection (GC)

### Life Cycle of a `ParkingTicket` Object:
1. **Creation (Heap Allocation):**
   - Triggered by `new CampusTicket(...)`. Memory allocated in Young Generation (Eden space). Reference stored in `t1` on method stack.
2. **Active State:**
   - Referenced simultaneously by the parking slot array (`slots[f][s]`) and the customer's active session.
3. **Checkout / Inactivation:**
   - Vehicle leaves. The ticket's `checkout()` is invoked; `isActive` becomes `false`.
   - The slot reference is cleared (`slots[floor][slot] = null;`).
4. **Eligibility for Garbage Collection:**
   - When the stack frame exits or local variables are nullified (`t1 = null;`), the ticket object on the heap has **zero reachable references** from GC Roots.
   - It becomes an unreachable island of isolation and is collected during the next Minor/Major GC sweep, recycling heap memory.

---

## 3. Object Passing Mechanism in Java (Pass-by-Value)

Java is **strictly pass-by-value**. When an object is passed into a method:
- The **value of the reference** (the memory pointer address) is copied onto the called method's stack frame.
- **Consequence of Reassignment:**
  ```java
  public static void modifyTicket(CampusTicket ticket, String newReg) {
      ticket = new CampusTicket(newReg); // Reassigns local copy only; caller's reference is untouched!
  }
  ```
- **Consequence of Field Mutation:**
  ```java
  public static void closeTicketInMethod(CampusTicket ticket) {
      ticket.checkout(); // Dereferences pointer and mutates shared heap object! Caller observes change.
  }
  ```

---

## 4. Alternative Concept and Complexity Analysis

### Static Counter vs Instance Counter in Multi-Facility Campus (North Gate vs South Gate)

Suppose the university operates $k$ facilities (e.g., North Gate and South Gate), and issues $n$ tickets total.

#### Scenario 1: Unprefixed Instance Counter
- Each gate maintains its own instance variable `long counter = 0;`.
- **Fatal Defect:** North Gate issues `TKT-1`, and South Gate simultaneously issues `TKT-1` for a different vehicle. When a vehicle presents `TKT-1` at checkout, the billing engine cannot resolve which vehicle entered where. Ticket ID uniqueness is violated!

#### Scenario 2: Global Static Counter
- Both North Gate and South Gate share a single `static long globalCounter`.
- North Gate issues `1001`, South Gate issues `1002`, North Gate issues `1003`.
- **Advantage:** Unconditionally unique across the entire campus.

#### Scenario 3: Namespaced / Prefixed Instance Counter
- Each gate prefixes its instance counter with its station ID (`"NORTH-1"`, `"SOUTH-1"`).
- **Advantage:** Independent offline operation without cross-gate locking.

#### Space and Time Complexity Analysis ($k$ gates, $n$ tickets):

| Strategy | Space Used by Counter | Uniqueness Guarantee | Offline Gate Fault-Tolerance |
| :--- | :--- | :--- | :--- |
| **Global Static Counter** | $O(1)$ space (8 bytes in Metaspace) | **Globally Unique** | Requires central sync/atomic increment |
| **Unprefixed Instance Counter** | $O(k)$ space (8 bytes $\times k$) | **COLLISION HAZARD** | Unsafe |
| **Prefixed Instance Counter** | $O(k)$ space (8 bytes $\times k$) | **Partitioned Unique** | High (gates operate independently) |

---

## 5. Execution and Verification
To compile and execute Task 5:
```bash
javac Task05/src/StaticLifecycleDemo.java
java -cp Task05/src StaticLifecycleDemo
```
Refer to `output.txt` for recorded verification.
