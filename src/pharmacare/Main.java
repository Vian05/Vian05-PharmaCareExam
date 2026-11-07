package pharmacare;

/**
 * Entry point for the PharmaCare console application.
 * This first milestone verifies the main navigation before business features are added.
 */
public class Main {
    private static final java.util.Scanner SCANNER = new java.util.Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMainMenu();
            System.out.print("Select menu: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1" -> pharmacistMenu();
                case "2" -> customerMenu();
                case "0" -> {
                    running = false;
                    System.out.println("Thank you for using PharmaCare.");
                }
                default -> System.out.println("Invalid menu selection. Please try again.");
            }
        }

        SCANNER.close();
    }

    /** Prints the main menu and available application roles. */
    private static void printMainMenu() {
        System.out.println("\n========================================");
        System.out.println("           PHARMACARE SYSTEM");
        System.out.println("       Pharmacy Management System");
        System.out.println("========================================");
        System.out.println("1. Pharmacist Login");
        System.out.println("2. Customer Menu");
        System.out.println("0. Exit");
        System.out.println("----------------------------------------");
    }

    /** Temporary pharmacist landing page; the dashboard is implemented in the next milestone. */
    private static void pharmacistMenu() {
        System.out.println("\n===== PHARMACIST DASHBOARD =====");
        System.out.println("Pharmacist features will be added in the next milestone.");
    }

    /** Temporary customer landing page; the dashboard is implemented in the next milestone. */
    private static void customerMenu() {
        System.out.println("\n===== CUSTOMER DASHBOARD =====");
        System.out.println("Customer features will be added in the next milestone.");
    }
}
