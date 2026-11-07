# PharmaCare Exam

Java 21 console-based Pharmacy Management System for the LO 3 PharmaCare assignment.

## Technology and design
- Java 21
- Console application (CLI)
- In-memory data storage using `ArrayList` (implemented in later milestones)
- Object-oriented programming: encapsulation, inheritance, polymorphism, abstract classes, and interfaces
- No external dependencies or build tool required

## Project structure

```text
src/
└── pharmacare/
    ├── Main.java
    └── InputHelper.java
```

## Compile and run

Run these commands from the repository root.

**macOS / Linux**
```bash
mkdir -p out
javac -d out src/pharmacare/*.java
java -cp out pharmacare.Main
```

**Windows (Command Prompt)**
```bat
if not exist out mkdir out
javac -d out src\pharmacare\*.java
java -cp out pharmacare.Main
```

## Current features\n- Pharmacist: medicine CRUD, restocking, and low-stock report.\n- Customer: browse catalog, add/remove cart items, update quantities, and view cart subtotal.\n- Prescription medicines can be added to the cart; prescription approval is required at checkout and will be implemented in a later milestone.\n\n## Implementation milestones
1. Project foundation and console navigation
2. Medicine model and pharmacist medicine/stock management
3. Customer catalog and shopping cart
4. Prescription validation and payment methods
5. Transactions, history, and sales reports
6. Testing, screenshots, and submission packaging

> Data is held in memory only. Data persistence is not retained after the application exits.
