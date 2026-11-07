package pharmacare;

import pharmacare.model.*;
import pharmacare.payment.*;
import pharmacare.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/** Dependency-free automated tests for PharmaCare business logic. */
public class PharmaCareTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       PHARMACARE AUTOMATED TEST");
        System.out.println("========================================");

        run("TC-02 Add Medicine", PharmaCareTest::testAddMedicine);
        run("TC-03 Duplicate Medicine ID", PharmaCareTest::testDuplicateMedicine);
        run("TC-04 Update Medicine", PharmaCareTest::testUpdateMedicine);
        run("TC-05 Delete Medicine", PharmaCareTest::testDeleteMedicine);
        run("TC-06 Restock Medicine", PharmaCareTest::testRestock);
        run("TC-07 Low Stock Report", PharmaCareTest::testLowStock);
        run("TC-08 Add Medicine to Cart", PharmaCareTest::testAddToCart);
        run("TC-09 Cart Quantity Exceeds Stock", PharmaCareTest::testCartExceedsStock);
        run("TC-10 Zero Cart Quantity", PharmaCareTest::testZeroQuantity);
        run("TC-11 Limited Medicine Valid Quantity", PharmaCareTest::testLimitedMedicineValid);
        run("TC-12 Limited Medicine Exceeds Limit", PharmaCareTest::testLimitedMedicineLimit);
        run("TC-13 Update Cart Quantity", PharmaCareTest::testUpdateCart);
        run("TC-14 Remove Medicine from Cart", PharmaCareTest::testRemoveFromCart);
        run("TC-15 Calculate Cart Subtotal", PharmaCareTest::testCartSubtotal);
        run("TC-16 Submit Prescription", PharmaCareTest::testSubmitPrescription);
        run("TC-17 Checkout Without Approved Prescription", PharmaCareTest::testCheckoutWithoutPrescription);
        run("TC-18 Approve Prescription", PharmaCareTest::testApprovePrescription);
        run("TC-19 Checkout with Approved Prescription", PharmaCareTest::testCheckoutWithPrescription);
        run("TC-20 Consume Prescription After Checkout", PharmaCareTest::testPrescriptionConsumed);
        run("TC-21 Prevent Prescription Reuse", PharmaCareTest::testPrescriptionReuse);
        run("TC-22 Cash Payment Fee", PharmaCareTest::testCashFee);
        run("TC-23 Card Payment Fee", PharmaCareTest::testCardFee);
        run("TC-24 QRIS Payment Fee", PharmaCareTest::testQrisFee);
        run("TC-25 Empty Cart Checkout", PharmaCareTest::testEmptyCartCheckout);
        run("TC-26 Stock Deduction", PharmaCareTest::testStockDeduction);
        run("TC-27 Cart Cleared After Checkout", PharmaCareTest::testCartCleared);
        run("TC-28 Transaction Created", PharmaCareTest::testTransactionCreated);
        run("TC-29 Customer Transaction History", PharmaCareTest::testCustomerHistory);
        run("TC-30 Daily Sales Report", PharmaCareTest::testDailyReport);
        run("TC-31 Monthly Sales Report", PharmaCareTest::testMonthlyReport);
        run("TC-32 Transaction Count", PharmaCareTest::testTransactionCount);
        run("TC-33 Best-selling Medicine", PharmaCareTest::testBestSelling);
        run("TC-34 Payment Breakdown", PharmaCareTest::testPaymentBreakdown);
        run("TC-35 Reject Zero Price", PharmaCareTest::testZeroPrice);
        run("TC-36 Reject Negative Stock", PharmaCareTest::testNegativeStock);
        run("TC-37 Reject Empty Medicine Name", PharmaCareTest::testEmptyMedicineName);
        run("TC-38 Reject Invalid Prescription Review", PharmaCareTest::testInvalidPrescriptionDecision);
        run("TC-39 Medicine Polymorphism", PharmaCareTest::testPolymorphism);
        run("TC-40 Payment Interface Implementations", PharmaCareTest::testPaymentInterface);

        System.out.println("----------------------------------------");
        System.out.println("Total Tests : " + (passed + failed));
        System.out.println("Passed      : " + passed);
        System.out.println("Failed      : " + failed);
        System.out.println("========================================");
        if (failed > 0) System.exit(1);
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable error) {
            failed++;
            System.out.println("[FAIL] " + name + " -> " + error.getMessage());
        }
    }

    private static FreeMedicine free(String id, String name, String price, int stock) {
        return new FreeMedicine(id, name, "Test description", new BigDecimal(price), stock);
    }

    private static LimitedMedicine limited(String id, int stock, int limit) {
        return new LimitedMedicine(id, "CTM", "Limited medicine", new BigDecimal("3000"), stock, limit);
    }

    private static PrescriptionMedicine prescriptionMedicine() {
        return new PrescriptionMedicine("MED-RX", "Amoxicillin", "Prescription medicine",
                new BigDecimal("12000"), 10);
    }

    private static Customer customer(String id) {
        return new Customer(id, "Test Customer");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void money(String expected, BigDecimal actual) {
        check(new BigDecimal(expected).compareTo(actual) == 0,
                "Expected " + expected + " but got " + actual);
    }

    private static void expectThrows(Class<? extends Throwable> type, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Expected " + type.getSimpleName()
                    + " but got " + error.getClass().getSimpleName());
        }
        throw new AssertionError("Expected " + type.getSimpleName() + " but no exception was thrown.");
    }

    private static Transaction checkout(ShoppingCart cart, Customer customer,
                                        PrescriptionService prescriptions, PaymentMethod payment,
                                        TransactionService transactions) {
        return new CheckoutService().checkout(customer, cart, prescriptions, payment, transactions);
    }

    private static void testAddMedicine() {
        PharmacyService service = new PharmacyService();
        service.addMedicine(free("M1", "Paracetamol", "5000", 20));
        check(service.getAllMedicines().size() == 1, "Medicine should be added.");
    }

    private static void testDuplicateMedicine() {
        PharmacyService service = new PharmacyService();
        service.addMedicine(free("M1", "A", "5000", 10));
        expectThrows(IllegalArgumentException.class,
                () -> service.addMedicine(free("M1", "B", "6000", 10)));
    }

    private static void testUpdateMedicine() {
        PharmacyService service = new PharmacyService();
        service.addMedicine(free("M1", "Old", "5000", 10));
        service.updateMedicine("M1", "New", "Updated", new BigDecimal("7000"), null);
        check(service.findMedicine("M1").getName().equals("New"), "Name was not updated.");
        money("7000", service.findMedicine("M1").getPrice());
    }

    private static void testDeleteMedicine() {
        PharmacyService service = new PharmacyService();
        service.addMedicine(free("M1", "A", "5000", 10));
        check(service.deleteMedicine("M1"), "Delete should return true.");
        check(service.findMedicine("M1") == null, "Medicine should no longer exist.");
    }

    private static void testRestock() {
        PharmacyService service = new PharmacyService();
        service.addMedicine(free("M1", "A", "5000", 8));
        service.restockMedicine("M1", 10);
        check(service.findMedicine("M1").getStock() == 18, "Expected stock 18.");
    }

    private static void testLowStock() {
        PharmacyService service = new PharmacyService();
        service.addMedicine(free("M1", "Low", "5000", 9));
        service.addMedicine(free("M2", "Enough", "5000", 10));
        check(service.getLowStockMedicines(10).size() == 1, "Only stock below 10 should be listed.");
    }

    private static void testAddToCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(free("M1", "A", "5000", 10), 2);
        check(cart.getItems().get(0).getQuantity() == 2, "Expected quantity 2.");
    }

    private static void testCartExceedsStock() {
        ShoppingCart cart = new ShoppingCart();
        expectThrows(IllegalArgumentException.class,
                () -> cart.addMedicine(free("M1", "A", "5000", 2), 3));
    }

    private static void testZeroQuantity() {
        ShoppingCart cart = new ShoppingCart();
        expectThrows(IllegalArgumentException.class,
                () -> cart.addMedicine(free("M1", "A", "5000", 10), 0));
    }

    private static void testLimitedMedicineValid() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(limited("M1", 10, 5), 5);
        check(cart.getItems().get(0).getQuantity() == 5, "Quantity 5 should be accepted.");
    }

    private static void testLimitedMedicineLimit() {
        ShoppingCart cart = new ShoppingCart();
        expectThrows(IllegalArgumentException.class,
                () -> cart.addMedicine(limited("M1", 10, 5), 6));
    }

    private static void testUpdateCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(free("M1", "A", "5000", 10), 1);
        cart.updateQuantity("M1", 3);
        check(cart.getItems().get(0).getQuantity() == 3, "Quantity should update to 3.");
    }

    private static void testRemoveFromCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(free("M1", "A", "5000", 10), 1);
        check(cart.removeMedicine("M1") && cart.isEmpty(), "Item should be removed.");
    }

    private static void testCartSubtotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(free("M1", "A", "5000", 10), 2);
        money("10000", cart.getSubtotal());
    }

    private static void testSubmitPrescription() {
        PrescriptionService service = new PrescriptionService();
        Prescription rx = service.submitPrescription("C1", "MED-RX", "/tmp/rx.jpg");
        check(rx.getStatus() == PrescriptionStatus.PENDING, "New prescription should be pending.");
    }

    private static void testCheckoutWithoutPrescription() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(prescriptionMedicine(), 1);
        expectThrows(IllegalArgumentException.class, () ->
                new CheckoutService().checkout(customer("C1"), cart, new PrescriptionService(),
                        new CashPayment(), new TransactionService()));
    }

    private static Prescription approvedPrescription(PrescriptionService service, String customerId) {
        Prescription rx = service.submitPrescription(customerId, "MED-RX", "/tmp/rx.jpg");
        service.reviewPrescription(rx.getId(), PrescriptionStatus.APPROVED);
        return rx;
    }

    private static void testApprovePrescription() {
        PrescriptionService service = new PrescriptionService();
        Prescription rx = service.submitPrescription("C1", "MED-RX", "/tmp/rx.jpg");
        service.reviewPrescription(rx.getId(), PrescriptionStatus.APPROVED);
        check(rx.getStatus() == PrescriptionStatus.APPROVED, "Prescription should be approved.");
    }

    private static void testCheckoutWithPrescription() {
        PrescriptionService service = new PrescriptionService();
        approvedPrescription(service, "C1");
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(prescriptionMedicine(), 1);
        Transaction trx = checkout(cart, customer("C1"), service, new CashPayment(), new TransactionService());
        check(trx.getStatus() == TransactionStatus.COMPLETED, "Checkout should complete.");
    }

    private static void testPrescriptionConsumed() {
        PrescriptionService service = new PrescriptionService();
        Prescription rx = approvedPrescription(service, "C1");
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(prescriptionMedicine(), 1);
        checkout(cart, customer("C1"), service, new CashPayment(), new TransactionService());
        check(rx.getStatus() == PrescriptionStatus.CONSUMED, "Prescription should be consumed.");
    }

    private static void testPrescriptionReuse() {
        PrescriptionService service = new PrescriptionService();
        approvedPrescription(service, "C1");
        ShoppingCart first = new ShoppingCart();
        first.addMedicine(prescriptionMedicine(), 1);
        checkout(first, customer("C1"), service, new CashPayment(), new TransactionService());
        ShoppingCart second = new ShoppingCart();
        second.addMedicine(prescriptionMedicine(), 1);
        expectThrows(IllegalArgumentException.class, () ->
                new CheckoutService().checkout(customer("C1"), second, service,
                        new CashPayment(), new TransactionService()));
    }

    private static void testCashFee() {
        money("0", new CashPayment().calculateFee(new BigDecimal("100000")));
    }

    private static void testCardFee() {
        money("2000", new CardPayment().calculateFee(new BigDecimal("100000")));
    }

    private static void testQrisFee() {
        money("700", new QRISPayment().calculateFee(new BigDecimal("100000")));
    }

    private static void testEmptyCartCheckout() {
        expectThrows(IllegalArgumentException.class, () ->
                new CheckoutService().checkout(customer("C1"), new ShoppingCart(),
                        new PrescriptionService(), new CashPayment(), new TransactionService()));
    }

    private static void testStockDeduction() {
        FreeMedicine medicine = free("M1", "A", "5000", 10);
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(medicine, 2);
        checkout(cart, customer("C1"), new PrescriptionService(), new CashPayment(), new TransactionService());
        check(medicine.getStock() == 8, "Stock should decrease to 8.");
    }

    private static void testCartCleared() {
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(free("M1", "A", "5000", 10), 1);
        checkout(cart, customer("C1"), new PrescriptionService(), new CashPayment(), new TransactionService());
        check(cart.isEmpty(), "Cart should be empty after checkout.");
    }

    private static TransactionService completedTransaction(String customerId, String medicineId,
                                                            String payment, String price, int quantity) {
        FreeMedicine medicine = free(medicineId, medicineId, price, 20);
        ShoppingCart cart = new ShoppingCart();
        cart.addMedicine(medicine, quantity);
        TransactionService service = new TransactionService();
        checkout(cart, customer(customerId), new PrescriptionService(),
                payment.equals("Card") ? new CardPayment()
                        : payment.equals("QRIS") ? new QRISPayment() : new CashPayment(), service);
        return service;
    }

    private static void testTransactionCreated() {
        TransactionService service = completedTransaction("C1", "M1", "Cash", "5000", 1);
        check(service.getAllTransactions().size() == 1, "One transaction should be recorded.");
        check(service.getAllTransactions().get(0).getId().equals("TRX-00001"), "Transaction ID mismatch.");
    }

    private static void testCustomerHistory() {
        TransactionService service = new TransactionService();
        Transaction trx = completedTransaction("C1", "M1", "Cash", "5000", 1).getAllTransactions().get(0);
        check(trx.getCustomerId().equals("C1"), "Customer ID snapshot mismatch.");
        check(service.getCustomerHistory("C1").isEmpty(), "New service should have no history.");
    }

    private static void testDailyReport() {
        TransactionService service = completedTransaction("C1", "M1", "Cash", "5000", 1);
        check(service.getDailyTransactions(LocalDate.now()).size() == 1, "Today's transaction should be found.");
    }

    private static void testMonthlyReport() {
        TransactionService service = completedTransaction("C1", "M1", "Cash", "5000", 1);
        check(service.getMonthlyTransactions(YearMonth.now()).size() == 1, "This month's transaction should be found.");
    }

    private static void testTransactionCount() {
        TransactionService service = completedTransaction("C1", "M1", "Cash", "5000", 1);
        check(service.getAllTransactions().size() == 1, "Expected one transaction.");
    }

    private static void testBestSelling() {
        TransactionService service = completedTransaction("C1", "M1", "Cash", "5000", 2);
        Map<String, Integer> top = service.getTopSellingMedicines(service.getAllTransactions());
        check(top.get("M1") == 2, "Expected M1 to have two units sold.");
    }

    private static void testPaymentBreakdown() {
        TransactionService service = completedTransaction("C1", "M1", "Cash", "5000", 1);
        Map<String, BigDecimal> breakdown = service.getPaymentBreakdown(service.getAllTransactions());
        money("5000", breakdown.get("Cash"));
    }

    private static void testZeroPrice() {
        expectThrows(IllegalArgumentException.class, () ->
                free("M1", "A", "0", 10));
    }

    private static void testNegativeStock() {
        expectThrows(IllegalArgumentException.class, () ->
                free("M1", "A", "5000", -1));
    }

    private static void testEmptyMedicineName() {
        expectThrows(IllegalArgumentException.class, () ->
                free("M1", " ", "5000", 10));
    }

    private static void testInvalidPrescriptionDecision() {
        PrescriptionService service = new PrescriptionService();
        Prescription rx = service.submitPrescription("C1", "MED-RX", "/tmp/rx.jpg");
        expectThrows(IllegalArgumentException.class, () ->
                service.reviewPrescription(rx.getId(), PrescriptionStatus.CONSUMED));
    }

    private static void testPolymorphism() {
        Medicine free = free("M1", "A", "5000", 10);
        Medicine rx = prescriptionMedicine();
        check(free.getCategory().equals("Obat Bebas"), "Free medicine category mismatch.");
        check(rx.getCategory().equals("Obat Keras"), "Prescription category mismatch.");
    }

    private static void testPaymentInterface() {
        PaymentMethod cash = new CashPayment();
        PaymentMethod card = new CardPayment();
        PaymentMethod qris = new QRISPayment();
        money("0", cash.calculateFee(new BigDecimal("100000")));
        money("2000", card.calculateFee(new BigDecimal("100000")));
        money("700", qris.calculateFee(new BigDecimal("100000")));
    }
}
