# Task 4 – Design Classes, Objects and Constructors
**Student Name:** Jaya Harish J  
**Register Number:** 192462001  
**Course Code & Title:** CSA09 – Java Programming / Object-Oriented Programming (CO1)  
**Bloom’s Level:** K4 – Analyse  
**Marks:** 4 Marks  

---

## 1. Class Design: Fields and Methods

### `ParkingTicket` Class Structure
- **Private Final Fields (Immutability & Safety):**
  - `long ticketId`: Unique monotonically increasing identification number.
  - `String regNo`: Alphanumeric vehicle registration plate string.
  - `int floor`: Floor index of the assigned bay.
  - `int slot`: Bay index on the assigned floor.
  - `long entryTimeMinutes`: Timestamp when the vehicle passed the entry barrier.
  - `String userType`: Affiliation category (`Student`, `Staff`, `Visitor`).
  - `boolean isActive`: Current lifecycle state of the ticket.
- **Methods:**
  - Standard accessor getters (`getTicketId()`, `getRegNo()`, `getFloor()`, `getSlot()`, etc.).
  - `close()`: Marks the ticket inactive upon exit clearance.
  - `toString()`: Provides diagnostic state visualization.

---

## 2. Constructor Hierarchy and the `this` Keyword

### 1. Canonical Parameterized Constructor
Contains comprehensive input validation to ensure that an invalid object can never be constructed:
```java
public ParkingTicket(long ticketId, String regNo, int floor, int slot,
                     long entryTimeMinutes, String userType, boolean isActive) {
    if (regNo == null || regNo.trim().isEmpty()) {
        throw new IllegalArgumentException("Registration cannot be null or empty.");
    }
    this.ticketId = ticketId;
    this.regNo = regNo.toUpperCase().trim();
    this.floor = floor;
    this.slot = slot;
    this.entryTimeMinutes = entryTimeMinutes;
    this.userType = (userType == null) ? "Visitor" : userType;
    this.isActive = isActive;
}
```

### 2. Constructor Chaining with `this(...)`
Instead of duplicating validation logic across multiple overloaded constructors, Java allows delegating from one constructor to another using `this(...)` as the very first executable statement:
```java
public ParkingTicket(long ticketId, String regNo, int floor, int slot) {
    this(ticketId, regNo, floor, slot, System.currentTimeMillis() / 60000L, "Visitor", true);
}
```
**Benefits:**
- Eliminates redundant field assignments.
- Centralizes defensive validation in exactly one canonical constructor.

### 3. Copy Constructor
A **copy constructor** takes an existing instance of the same class and duplicates its state:
```java
public ParkingTicket(ParkingTicket other) {
    this.ticketId = other.ticketId;
    this.regNo = other.regNo;
    this.floor = other.floor;
    this.slot = other.slot;
    this.entryTimeMinutes = other.entryTimeMinutes;
    this.userType = other.userType;
    this.isActive = other.isActive;
}
```
**Why it is needed in this system:**
- **Duplicate Ticket Re-issuance:** If a visitor loses their paper receipt or requests a verified copy at the helpdesk, a duplicate ticket object is instantiated.
- **Defensive State Snapshotting:** When calculating the exit bill, creating a snapshot prevents the ongoing billing transaction from mutating or contaminating active facility ticket records.

---

## 3. Memory Layout: Stack vs Heap

In Java, execution memory is cleanly divided into the **Call Stack** and the **Managed Heap**:
1. **Thread Stack Frame:**
   - When `ParkingTicket t1 = new ParkingTicket(1001L, "TN01AA1111", 0, 1);` executes:
   - The reference variable `t1` is stored in the local variable table of the active method stack frame.
   - For a 64-bit JVM with Compressed OOPs (Ordinary Object Pointers), `t1` occupies **4 to 8 bytes** on the stack.
2. **Managed Heap:**
   - The actual `ParkingTicket` object instance is allocated on the Heap.
   - **Heap Memory Breakdown of `ParkingTicket`:**
     - **Object Header (16 bytes):** Mark Word (8 bytes: hash code, GC age, lock bits) + Klass Word (8 bytes / 4 bytes compressed: pointer to class metadata).
     - **Primitive Fields:** `long ticketId` (8 bytes), `int floor` (4 bytes), `int slot` (4 bytes), `long entryTimeMinutes` (8 bytes), `boolean isActive` (1 byte + 3 bytes padding).
     - **Reference Fields:** `String regNo` (4/8 bytes pointer), `String userType` (4/8 bytes pointer).
     - Total instance size $\approx 48$ bytes on 64-bit heap (+ referenced string objects).

```
   STACK FRAME (main)                          HEAP MEMORY
+------------------------+          +------------------------------------+
|  Variable  |  Value    |          | ParkingTicket Object (at 0x4F0A)   |
|------------|-----------|          |------------------------------------|
|  t1        | 0x4F0A ----------->  | Header: Mark Word, Klass Pointer   |
|  t2        | 0x51B0 ----\         | ticketId: 1001L                    |
|                        |          | regNo: 0x8890 ("TN01AA1111")       |
|                        |          | floor: 0, slot: 1                  |
|                        |          | entryTimeMinutes: 29873663         |
|                        |          | isActive: true                     |
+------------------------+          +------------------------------------+
                                                   |
                                    +------------------------------------+
                                    | ParkingTicket Object (at 0x51B0)   |
                                    +------------------------------------+
```

---

## 4. Alternative Concept and Complexity Analysis

### Comparison: Constructor Chaining vs JavaBeans (Default + Setters) vs Static Factory Methods

#### 1. JavaBeans Pattern (`new Bean(); b.setX(...);`):
- **Fatal Flaw:** Objects exist in a **partially initialized, inconsistent state** between the `new` call and the subsequent setters. If an exception occurs or another thread reads the object during setter calls, fields may be null or corrupted.
- **Immutability Loss:** Cannot declare fields as `final`, compromising thread safety.

#### 2. Static Factory Methods (`ParkingTicket.issueVisitorTicket(...)`):
- **Advantages:** Meaningful business names, can return cached instances or subtypes without changing caller syntax.
- Internally still uses constructor chaining.

#### Analytical Comparison for Creating $n$ Ticket Objects:

| Metric | Constructor Chaining (Master Constructor) | JavaBeans (Default + Setters) | Static Factory Methods |
| :--- | :--- | :--- | :--- |
| **Object Invariant Guarantee** | **100% Guaranteed** (Immediate validation) | **Vulnerable** (Partially constructed) | **100% Guaranteed** |
| **Field Immutability (`final`)** | Yes, fully supported | No, requires mutable fields | Yes, fully supported |
| **Time to create $n$ tickets** | $O(n)$ ($1$ method invocation per object) | $O(n)$ ($k$ setter invocations per object) | $O(n)$ |
| **Heap Space for $n$ tickets** | $O(n)$ ($1$ heap allocation per ticket) | $O(n)$ ($1$ heap allocation per ticket) | $O(n)$ |
| **Bytecode Instructions** | Minimal: `new`, `dup`, `invokespecial` | Bloated: `new`, `dup`, `invokespecial`, $k \times \text{invokevirtual}$ | Minimal delegation |

---

## 5. Execution and Verification
To compile and execute Task 4:
```bash
javac Task04/src/ConstructorChainingDemo.java
java -cp Task04/src ConstructorChainingDemo
```
Refer to `output.txt` for recorded verification.
