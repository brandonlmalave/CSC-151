import java.util.Scanner;

/**
 * Safe input reading. Keeps asking until the user types something valid,
 * so the rest of the program never has to deal with bad input.
 */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Reads non-empty text. */
    public String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("This can't be blank. Try again.");
        }
    }

    /** Reads a whole number between min and max (inclusive). */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("That's not a whole number. Try again.");
            }
        }
    }

    /** Reads a decimal number greater than 0 (or >= 0 if allowZero). */
    public double readPositiveDouble(String prompt, boolean allowZero) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (value > 0 || (allowZero && value == 0)) {
                    return value;
                }
                System.out.println(allowZero ? "Can't be negative. Try again." : "Must be greater than 0. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("That's not a number. Try again.");
            }
        }
    }

    /** Reads y/n and returns true for yes. */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.startsWith("y")) {
                return true;
            }
            if (line.startsWith("n")) {
                return false;
            }
            System.out.println("Please type y or n.");
        }
    }
}