package pharmacare;

import pharmacare.model.FreeMedicine;
import pharmacare.model.LimitedMedicine;
import pharmacare.model.Medicine;
import pharmacare.model.PrescriptionMedicine;
import pharmacare.service.PharmacyService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/** Entry point for the PharmaCare console application. */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final PharmacyService PHARMACY_SERVICE = new PharmacyService();
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
            System.out.println("0. Back to Main Menu");
            int choice = InputHelper.readIntInRange(SCANNER, "Select menu: ", 0, 6);
            try {
                switch (choice) {
                    case 1 -> addMedicineMenu();
                    case 2 -> displayMedicines(PHARMACY_SERVICE.getAllMedicines());
                    case 3 -> editMedicineMenu();
                    case 4 -> deleteMedicineMenu();
                    case 5 -> restockMedicineMenu();
                    case 6 -> displayMedicines(PHARMACY_SERVICE.getLowStockMedicines(LOW_STOCK_THRESHOLD));
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

    /** Displays the customer placeholder until customer features are implemented. */
    private static void customerMenu() {
        System.out.println("\n===== CUSTOMER =====");
        System.out.println("Customer features will be implemented in the next milestone.");
        InputHelper.readNonEmpty(SCANNER, "Enter any text to return to the main menu: ");
    }
}
