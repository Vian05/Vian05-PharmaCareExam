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
    │   ├── ShoppingCart.java
    │   ├── Prescription.java
    │   ├── PrescriptionStatus.java
    │   ├── Transaction.java
    │   ├── TransactionItem.java
    │   └── TransactionStatus.java
    ├── payment/
    │   ├── PaymentMethod.java
    │   ├── CashPayment.java
    │   ├── CardPayment.java
    │   └── QRISPayment.java
    └── service/
        ├── PharmacyService.java
        ├── PrescriptionService.java
        ├── CheckoutService.java
        └── TransactionService.java
```

## Current features
- Pharmacist: medicine CRUD, restocking, low-stock report, prescription list, and prescription approval/rejection.
- Customer: enter a reusable customer ID, browse catalog, manage shopping cart, submit a prescription file path for a specific Obat Keras medicine, checkout, and view transaction history.
- Obat Keras checkout is blocked until a pharmacist approves a submitted prescription.
- An approved prescription is marked consumed after successful checkout.
- Payment options: Cash (0% fee), Card (2% fee), and QRIS (0.7% fee). These are simulation assumptions for the assignment, not official provider rates.
- Pharmacist: daily and monthly sales reports with transaction count, revenue, top three best-selling medicines, and payment method breakdown.
- Payment is simulated as successful; no external payment gateway or real file upload is connected.
- Transaction history and reports are stored in memory and reset when the application exits.

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
