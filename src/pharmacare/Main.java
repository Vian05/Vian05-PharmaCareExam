package pharmacare;

import pharmacare.model.CartItem;
import pharmacare.model.Customer;
import pharmacare.model.FreeMedicine;
import pharmacare.model.LimitedMedicine;
import pharmacare.model.Medicine;
import pharmacare.model.Prescription;
import pharmacare.model.PrescriptionMedicine;
import pharmacare.model.PrescriptionStatus;
import pharmacare.model.ShoppingCart;
import pharmacare.service.CheckoutService;
import pharmacare.service.PharmacyService;
import pharmacare.service.PrescriptionService;
import pharmacare.payment.CashPayment;
import pharmacare.payment.CardPayment;
import pharmacare.payment.PaymentMethod;
import pharmacare.payment.QRISPayment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

/** Entry point for the PharmaCare console application. */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final PharmacyService PHARMACY_SERVICE = new PharmacyService();
    private static final PrescriptionService PRESCRIPTION_SERVICE = new PrescriptionService();
    private static final CheckoutService CHECKOUT_SERVICE = new CheckoutService();
    private static final Map<String, ShoppingCart> CUSTOMER_CARTS = new HashMap<>();
    private static final int LOW_STOCK_THRESHOLD = 10;

    public static void main(String[] args) {
        seedSampleMedicines();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = InputHelper.readNonEmpty(SCANNER, "Select menu: ");
            switch (choice) {
                case "1" -> pharmacistMenu();
                case "2" -> customerMenu();
                case "0" -> {
                    running = false;
                    System.out.println("\nThank you for using PharmaCare.");
                }
                default -> System.out.println("Invalid menu selection. Please enter 0, 1, or 2.");
            }
        }
    }

    /** Adds sample records for initial catalog testing. */
    private static void seedSampleMedicines() {
        PHARMACY_SERVICE.addMedicine(new FreeMedicine("MED001", "Paracetamol 500mg",
                "Pain and fever relief", new BigDecimal("5000"), 25));
        PHARMACY_SERVICE.addMedicine(new LimitedMedicine("MED002", "CTM 4mg",
                "Antihistamine medicine", new BigDecimal("3000"), 8, 5));
        PHARMACY_SERVICE.addMedicine(new PrescriptionMedicine("MED003", "Amoxicillin 500mg",
                "Antibiotic medicine", new BigDecimal("12000"), 15));
    }

    /** Displays the main application menu. */
    private static void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("             PHARMACARE");
        System.out.println("       Pharmacy Management System");
        System.out.println("========================================");
        System.out.println("1. Pharmacist / Admin");
        System.out.println("2. Customer");
        System.out.println("0. Exit");
        System.out.println("----------------------------------------");
    }

    /** Displays pharmacist medicine and stock management options. */
    private static void pharmacistMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n===== PHARMACIST / ADMIN =====");
            System.out.println("1. Add Medicine");
            System.out.println("2. View Medicine Catalog");
            System.out.println("3. Edit Medicine");
            System.out.println("4. Delete Medicine");
            System.out.println("5. Restock Medicine");
            System.out.println("6. Low Stock Report (< " + LOW_STOCK_THRESHOLD + ")");
            System.out.println("7. View Prescriptions");
            System.out.println("8. Review Prescription");
            System.out.println("0. Back to Main Menu");
            int choice = InputHelper.readIntInRange(SCANNER, "Select menu: ", 0, 8);
            try {
                switch (choice) {
                    case 1 -> addMedicineMenu();
                    case 2 -> displayMedicines(PHARMACY_SERVICE.getAllMedicines());
                    case 3 -> editMedicineMenu();
                    case 4 -> deleteMedicineMenu();
                    case 5 -> restockMedicineMenu();
                    case 6 -> displayMedicines(PHARMACY_SERVICE.getLowStockMedicines(LOW_STOCK_THRESHOLD));
                    case 7 -> displayPrescriptions();
                    case 8 -> reviewPrescriptionMenu();
                    case 0 -> inMenu = false;
                    default -> System.out.println("Invalid menu selection.");
                }
            } catch (IllegalArgumentException | ArithmeticException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    /** Collects details and creates a medicine for the selected category. */
    private static void addMedicineMenu() {
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID: ");
        String name = InputHelper.readNonEmpty(SCANNER, "Medicine name: ");
        String description = InputHelper.readNonEmpty(SCANNER, "Description: ");
        BigDecimal price = readPositivePrice();
        int stock = InputHelper.readIntInRange(SCANNER, "Initial stock: ", 0, Integer.MAX_VALUE);
        System.out.println("Category: 1. Obat Bebas  2. Obat Bebas Terbatas  3. Obat Keras");
        int category = InputHelper.readIntInRange(SCANNER, "Select category: ", 1, 3);

        Medicine medicine = switch (category) {
            case 1 -> new FreeMedicine(id, name, description, price, stock);
            case 2 -> new LimitedMedicine(id, name, description, price, stock,
                    InputHelper.readIntInRange(SCANNER, "Maximum purchase quantity: ", 1, Integer.MAX_VALUE));
            case 3 -> new PrescriptionMedicine(id, name, description, price, stock);
            default -> throw new IllegalStateException("Unexpected category.");
        };
        PHARMACY_SERVICE.addMedicine(medicine);
        System.out.println("Medicine added successfully.");
    }

    /** Updates common medicine details and the limited-category purchase cap. */
    private static void editMedicineMenu() {
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID to edit: ");
        Medicine existing = PHARMACY_SERVICE.findMedicine(id);
        if (existing == null) {
            System.out.println("Medicine not found.");
            return;
        }
        String name = InputHelper.readNonEmpty(SCANNER, "New name: ");
        String description = InputHelper.readNonEmpty(SCANNER, "New description: ");
        BigDecimal price = readPositivePrice();
        Integer limit = existing instanceof LimitedMedicine
                ? InputHelper.readIntInRange(SCANNER, "New maximum purchase quantity: ", 1, Integer.MAX_VALUE)
                : null;
        PHARMACY_SERVICE.updateMedicine(id, name, description, price, limit);
        System.out.println("Medicine updated successfully.");
    }

    /** Deletes a medicine after confirmation. */
    private static void deleteMedicineMenu() {
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID to delete: ");
        String confirmation = InputHelper.readNonEmpty(SCANNER, "Confirm deletion (Y/N): ");
        if (confirmation.equalsIgnoreCase("Y")) {
            System.out.println(PHARMACY_SERVICE.deleteMedicine(id)
                    ? "Medicine deleted successfully." : "Medicine not found.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /** Adds stock to an existing medicine. */
    private static void restockMedicineMenu() {
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID to restock: ");
        int quantity = InputHelper.readIntInRange(SCANNER, "Restock quantity: ", 1, Integer.MAX_VALUE);
        PHARMACY_SERVICE.restockMedicine(id, quantity);
        System.out.println("Stock updated successfully.");
    }

    /** Displays medicine records or an empty-result message. */
    private static void displayMedicines(List<Medicine> medicines) {
        if (medicines.isEmpty()) {
            System.out.println("No medicines found.");
            return;
        }
        System.out.println("\nID | Name | Category | Price (IDR) | Stock | Purchase Rule");
        System.out.println("--------------------------------------------------------------------------");
        for (Medicine medicine : medicines) System.out.println(medicine);
    }

    /** Reads a positive price using BigDecimal to avoid floating-point currency errors. */
    private static BigDecimal readPositivePrice() {
        while (true) {
            String value = InputHelper.readNonEmpty(SCANNER, "Price (IDR): ");
            try {
                BigDecimal price = new BigDecimal(value);
                if (price.compareTo(BigDecimal.ZERO) > 0) return price;
            } catch (NumberFormatException ignored) {
                // Continue to the validation message.
            }
            System.out.println("Please enter a valid price greater than zero.");
        }
    }

    /** Starts a customer session with a new in-memory shopping cart. */
    private static void customerMenu() {
        String customerId = InputHelper.readNonEmpty(SCANNER, "Customer ID (reuse the same ID for future sessions): ");
        String customerName = InputHelper.readNonEmpty(SCANNER, "Customer name: ");
        Customer customer = new Customer(customerId, customerName);
        ShoppingCart cart = CUSTOMER_CARTS.computeIfAbsent(customer.getId().toLowerCase(), key -> new ShoppingCart());
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n===== CUSTOMER MENU: " + customer.getName() + " =====");
            System.out.println("1. View Medicine Catalog");
            System.out.println("2. Add Medicine to Cart");
            System.out.println("3. View Shopping Cart");
            System.out.println("4. Update Cart Quantity");
            System.out.println("5. Remove Medicine from Cart");
            System.out.println("6. Submit Prescription");
            System.out.println("7. Checkout");
            System.out.println("0. Back to Main Menu");
            int choice = InputHelper.readIntInRange(SCANNER, "Select menu: ", 0, 7);

            try {
                switch (choice) {
                    case 1 -> displayMedicines(PHARMACY_SERVICE.getAllMedicines());
                    case 2 -> addToCartMenu(cart);
                    case 3 -> displayCart(cart);
                    case 4 -> updateCartMenu(cart);
                    case 5 -> removeFromCartMenu(cart);
                    case 6 -> submitPrescriptionMenu(customer);
                    case 7 -> checkoutMenu(customer, cart);
                    case 0 -> inMenu = false;
                    default -> System.out.println("Invalid menu selection.");
                }
            } catch (IllegalArgumentException | ArithmeticException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    /** Adds a selected catalog medicine to the shopping cart. */
    private static void addToCartMenu(ShoppingCart cart) {
        displayMedicines(PHARMACY_SERVICE.getAllMedicines());
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID to add: ");
        Medicine medicine = PHARMACY_SERVICE.findMedicine(id);
        if (medicine == null) {
            System.out.println("Medicine not found.");
            return;
        }
        int quantity = InputHelper.readIntInRange(SCANNER, "Quantity: ", 1, Integer.MAX_VALUE);
        cart.addMedicine(medicine, quantity);
        System.out.println("Medicine added to cart.");
        if (medicine instanceof PrescriptionMedicine) {
            System.out.println("Notice: This medicine requires an approved prescription before checkout.");
        }
    }

    /** Displays cart items and the subtotal before payment fees. */
    private static void displayCart(ShoppingCart cart) {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty.");
            return;
        }
        System.out.println("\nMedicine | Quantity | Unit Price (IDR) | Subtotal (IDR)");
        System.out.println("------------------------------------------------------");
        for (CartItem item : cart.getItems()) {
            System.out.printf("%s | %d | %s | %s%n", item.getMedicine().getName(),
                    item.getQuantity(), item.getMedicine().getPrice().toPlainString(),
                    item.getSubtotal().toPlainString());
        }
        System.out.println("Cart subtotal: IDR " + cart.getSubtotal().toPlainString());
    }

    /** Updates the quantity of a medicine already in the cart. */
    private static void updateCartMenu(ShoppingCart cart) {
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID to update: ");
        int quantity = InputHelper.readIntInRange(SCANNER, "New quantity: ", 1, Integer.MAX_VALUE);
        cart.updateQuantity(id, quantity);
        System.out.println("Cart quantity updated.");
    }


    /** Registers a simulated prescription file path for pharmacist review. */
    private static void submitPrescriptionMenu(Customer customer) {
        String filePath = InputHelper.readNonEmpty(SCANNER, "Prescription file path: ");
        Prescription prescription = PRESCRIPTION_SERVICE.submitPrescription(customer.getId(), filePath);
        System.out.println("Prescription submitted. Reference ID: " + prescription.getId());
        System.out.println("Status: PENDING. Pharmacist approval is required before checkout.");
    }

    /** Displays submitted prescriptions for pharmacist review. */
    private static void displayPrescriptions() {
        List<Prescription> prescriptions = PRESCRIPTION_SERVICE.getAllPrescriptions();
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions submitted.");
            return;
        }
        System.out.println("\nPrescription ID | Customer ID | File Path | Status");
        for (Prescription prescription : prescriptions) {
            System.out.printf("%s | %s | %s | %s%n", prescription.getId(),
                    prescription.getCustomerId(), prescription.getFilePath(), prescription.getStatus());
        }
    }

    /** Approves or rejects a pending prescription. */
    private static void reviewPrescriptionMenu() {
        displayPrescriptions();
        String id = InputHelper.readNonEmpty(SCANNER, "Prescription ID to review: ");
        System.out.println("1. Approve  2. Reject");
        int decision = InputHelper.readIntInRange(SCANNER, "Select decision: ", 1, 2);
        PrescriptionStatus status = decision == 1 ? PrescriptionStatus.APPROVED : PrescriptionStatus.REJECTED;
        PRESCRIPTION_SERVICE.reviewPrescription(id, status);
        System.out.println("Prescription " + id + " marked as " + status + ".");
    }

    /** Selects a payment method, shows the fee, and processes checkout. */
    private static void checkoutMenu(Customer customer, ShoppingCart cart) {
        if (cart.isEmpty()) {
            System.out.println("Shopping cart is empty.");
            return;
        }
        displayCart(cart);
        CHECKOUT_SERVICE.validateCheckout(customer, cart, PRESCRIPTION_SERVICE);
        System.out.println("Payment method: 1. Cash (0%)  2. Card (2%)  3. QRIS (0.7%)");
        int choice = InputHelper.readIntInRange(SCANNER, "Select payment method: ", 1, 3);
        PaymentMethod paymentMethod = switch (choice) {
            case 1 -> new CashPayment();
            case 2 -> new CardPayment();
            case 3 -> new QRISPayment();
            default -> throw new IllegalStateException("Unexpected payment method.");
        };
        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal fee = CHECKOUT_SERVICE.calculateFee(cart, paymentMethod);
        BigDecimal total = subtotal.add(fee);
        System.out.println("Subtotal: IDR " + subtotal.toPlainString());
        System.out.println("Payment fee (" + paymentMethod.getName() + "): IDR " + fee.toPlainString());
        System.out.println("Total: IDR " + total.toPlainString());
        String confirm = InputHelper.readNonEmpty(SCANNER, "Confirm payment (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Checkout cancelled. Cart and stock remain unchanged.");
            return;
        }
        BigDecimal paidTotal = CHECKOUT_SERVICE.checkout(customer, cart, PRESCRIPTION_SERVICE, paymentMethod);
        System.out.println("Payment successful. Paid: IDR " + paidTotal.toPlainString());
        System.out.println("Order completed. Stock has been updated.");
    }

    /** Removes a selected medicine from the shopping cart. */
    private static void removeFromCartMenu(ShoppingCart cart) {
        String id = InputHelper.readNonEmpty(SCANNER, "Medicine ID to remove: ");
        System.out.println(cart.removeMedicine(id)
                ? "Medicine removed from cart." : "Medicine was not found in the cart.");
    }
}
