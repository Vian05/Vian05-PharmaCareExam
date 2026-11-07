# PharmaCare Exam

Java 21 console-based Pharmacy Management System for the LO 3 PharmaCare assignment.

## Technology and design
- Java 21
- Console application (CLI)
- In-memory data storage using `ArrayList`
- Object-oriented programming: encapsulation, inheritance, polymorphism, abstract classes, and interfaces
- No external dependencies or build tool required

## Project structure

```text
src/
└── pharmacare/
    ├── Main.java
    ├── InputHelper.java
    ├── model/
    │   ├── Medicine.java
    │   ├── FreeMedicine.java
    │   ├── LimitedMedicine.java
    │   ├── PrescriptionMedicine.java
    │   ├── Customer.java
    │   ├── CartItem.java
    │   └── ShoppingCart.java
    └── service/
        └── PharmacyService.java
```

## Current features
- Pharmacist: medicine CRUD, restocking, and low-stock report.
- Customer: browse catalog, add/remove cart items, update quantities, and view cart subtotal.
- Prescription medicines can be added to the cart; prescription approval is required at checkout and will be implemented in a later milestone.

## Compile and run

Run these commands from the repository root.

**macOS / Linux**
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out pharmacare.Main
```

**Windows (PowerShell)**
```powershell
New-Item -ItemType Directory -Force out
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp out pharmacare.Main
```

## Implementation milestones
1. Project foundation and console navigation
2. Medicine model and pharmacist medicine/stock management
3. Customer catalog and shopping cart
4. Prescription validation and payment methods
5. Transactions, history, and sales reports
6. Testing, screenshots, and submission packaging

> Data is held in memory only. Data persistence is not retained after the application exits.
