package pharmacare;

import java.util.Scanner;

/** Provides reusable and validated console input methods. */
public final class InputHelper {
    private InputHelper() {
        // Utility class; no instances are required.
    }

    /** Reads input until a non-empty value is entered. */
    public static String readNonEmpty(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /** Reads an integer within the specified inclusive range. */
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String value = readNonEmpty(scanner, prompt);

            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // Show the same validation message for non-numeric input.
            }

            System.out.printf("Please enter a whole number between %d and %d.%n", min, max);
        }
    }

    /** Reads a positive monetary or quantity value represented by a decimal number. */
    public static double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            String value = readNonEmpty(scanner, prompt);

            try {
                double number = Double.parseDouble(value);
                if (Double.isFinite(number) && number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // Continue to the validation message below.
            }

            System.out.println("Please enter a valid number greater than zero.");
        }
    }
}
