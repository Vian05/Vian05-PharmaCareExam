package pharmacare;

import java.util.Scanner;

/**
 * Entry point for the PharmaCare console application.
 * Provides role navigation; business features are added in later milestones.
 */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
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

    /** Displays the pharmacist landing page until the user returns. */
    private static void pharmacistMenu() {
        showPlaceholderMenu("PHARMACIST / ADMIN");
    }

    /** Displays the customer landing page until the user returns. */
    private static void customerMenu() {
        showPlaceholderMenu("CUSTOMER");
    }

    /** Shows a temporary role page and waits for the user to return to the main menu. */
    private static void showPlaceholderMenu(String title) {
        System.out.println("\n===== " + title + " =====");
        System.out.println("Features will be implemented in the upcoming milestones.");
        InputHelper.readNonEmpty(SCANNER, "Press Enter to return to the main menu: ");
    }
}
