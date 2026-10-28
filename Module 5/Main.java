import java.util.List;
import java.util.Scanner;

/**
 * Entry point. Runs the console menu and ties everything together.
 */
public class Main {

    private static final String CSV_FILE = "labor_data.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InputHelper input = new InputHelper(scanner);
        CsvManager csv = new CsvManager(CSV_FILE);

        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.readInt("Choose an option: ", 1, 4);

            switch (choice) {
                case 1 -> createNewEstimate(input, csv);
                case 2 -> viewSavedLabor(csv);
                case 3 -> System.out.println("Coming soon: load a saved job.");
                case 4 -> running = false;
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== Concrete Pad Estimator ===");
        System.out.println("1. New estimate");
        System.out.println("2. View saved labor records");
        System.out.println("3. Load saved job (stretch goal)");
        System.out.println("4. Quit");
    }

    private static void createNewEstimate(InputHelper input, CsvManager csv) {
        // 1. Project info
        System.out.println();
        System.out.println("--- Project Info ---");
        String name = input.readText("Project name: ");
        String location = input.readText("Location: ");
        String client = input.readText("Client: ");
        Project project = new Project(name, location, client);

        // 2. Slab type and dimensions
        System.out.println();
        System.out.println("--- Slab Type ---");
        SlabType[] types = SlabType.values();
        for (int i = 0; i < types.length; i++) {
            SlabType t = types[i];
            if (t == SlabType.CUSTOM) {
                System.out.println((i + 1) + ". " + t.getDisplayName() + " (enter your own thickness)");
            } else {
                System.out.println((i + 1) + ". " + t.getDisplayName() + " (" + t.getDefaultThicknessInches() + " in)");
            }
        }
        int typeChoice = input.readInt("Choose a slab type: ", 1, types.length);
        SlabType slabType = types[typeChoice - 1];

        double length = input.readPositiveDouble("Length (ft): ", false);
        double width = input.readPositiveDouble("Width (ft): ", false);

        double thickness;
        if (slabType == SlabType.CUSTOM) {
            thickness = input.readPositiveDouble("Thickness (in): ", false);
        } else {
            thickness = slabType.getDefaultThicknessInches();
            System.out.println("Using " + thickness + " in thickness for " + slabType.getDisplayName() + ".");
        }

        // 3. Labor
        System.out.println();
        System.out.println("--- Labor ---");
        int workers = input.readInt("Number of workers: ", 1, 1000);
        double hours = input.readPositiveDouble("Hours per worker: ", false);
        double rate = input.readPositiveDouble("Hourly rate ($): ", false);
        LaborInfo labor = new LaborInfo(workers, hours, rate);

        // 4. Pricing
        System.out.println();
        System.out.println("--- Pricing ---");
        double pricePerYard = input.readPositiveDouble("Concrete price per cubic yard ($): ", false);
        double wastePercent = input.readPositiveDouble("Waste percentage (e.g. 10): ", true);

        // 5. Discount
        Discount discount = Discount.none();
        if (input.readYesNo("Apply a discount? (y/n): ")) {
            System.out.println("1. Percentage off");
            System.out.println("2. Fixed dollar amount off");
            int dChoice = input.readInt("Choose discount type: ", 1, 2);
            if (dChoice == 1) {
                double pct = input.readPositiveDouble("Percent off (e.g. 10): ", false);
                discount = new Discount(Discount.Type.PERCENT, pct);
            } else {
                double amt = input.readPositiveDouble("Dollars off: ", false);
                discount = new Discount(Discount.Type.FIXED, amt);
            }
        }

        // 6. Build and show the estimate
        Estimate estimate = new Estimate(project, slabType, length, width, thickness,
                labor, pricePerYard, wastePercent, discount);
        estimate.printSummary();

        // 7. Save labor info to CSV
        if (input.readYesNo("Save this job's labor info? (y/n): ")) {
            csv.saveLabor(project.getName(), labor);
        }
    }

    private static void viewSavedLabor(CsvManager csv) {
        List<CsvManager.LaborRecord> records = csv.loadAll();
        System.out.println();
        System.out.println("--- Saved Labor Records ---");
        if (records.isEmpty()) {
            System.out.println("No saved records yet.");
            return;
        }
        for (CsvManager.LaborRecord r : records) {
            System.out.printf("%s: %d workers x %.1f hrs @ $%.2f/hr = %.1f hrs, $%,.2f%n",
                    r.projectName,
                    r.labor.getWorkers(),
                    r.labor.getHoursPerWorker(),
                    r.labor.getHourlyRate(),
                    r.labor.getTotalHours(),
                    r.labor.getLaborCost());
        }
    }
}
/**
 * Basic info about the job. No math lives here.
 */
public class Project {

    private String name;
    private String location;
    private String client;

    public Project(String name, String location, String client) {
        this.name = name;
        this.location = location;
        this.client = client;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getClient() { return client; }

    // TODO (optional): add a date field, e.g. java.time.LocalDate
}

/**
 * Preset slab types (extra feature). Each one has a typical thickness.
 * Adjust the numbers to whatever your instructor / Jim wants.
 */
public enum SlabType {
    RESIDENTIAL_PAD("Residential pad", 4.0),
    DRIVEWAY("Driveway", 5.0),
    WAREHOUSE_SLAB("Warehouse slab", 6.0),
    HEAVY_INDUSTRIAL("Heavy industrial", 8.0),
    CUSTOM("Custom", 0.0); // user enters their own thickness

    private final String displayName;
    private final double defaultThicknessInches;

    SlabType(String displayName, double defaultThicknessInches) {
        this.displayName = displayName;
        this.defaultThicknessInches = defaultThicknessInches;
    }

    public String getDisplayName() { return displayName; }
    public double getDefaultThicknessInches() { return defaultThicknessInches; }
}


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads labor data (manpower + work hours) to/from a CSV file.
 *
 * File format, one job per line:
 *   projectName,workers,hoursPerWorker,hourlyRate
 */
public class CsvManager {

    private static final String HEADER = "projectName,workers,hoursPerWorker,hourlyRate";

    private final String filePath;

    public CsvManager(String filePath) {
        this.filePath = filePath;
    }

    /** Appends one job's labor info. Writes the header first if the file is new. */
    public void saveLabor(String projectName, LaborInfo labor) {
        File file = new File(filePath);
        boolean isNew = !file.exists();

        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            if (isNew) {
                out.println(HEADER);
            }
            String safeName = projectName.replace(",", " ");
            out.println(safeName + "," + labor.getWorkers() + ","
                    + labor.getHoursPerWorker() + "," + labor.getHourlyRate());
            System.out.println("Saved to " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving to CSV: " + e.getMessage());
        }
    }

    /** Reads every saved job. Returns an empty list if the file doesn't exist yet. */
    public List<LaborRecord> loadAll() {
        List<LaborRecord> records = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return records;
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = in.readLine(); // skip header
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    continue; // skip broken rows
                }
                try {
                    String name = parts[0];
                    int workers = Integer.parseInt(parts[1].trim());
                    double hours = Double.parseDouble(parts[2].trim());
                    double rate = Double.parseDouble(parts[3].trim());
                    records.add(new LaborRecord(name, new LaborInfo(workers, hours, rate)));
                } catch (NumberFormatException e) {
                    // skip rows with bad numbers
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return records;
    }

    /** One row from the CSV. */
    public static class LaborRecord {
        public final String projectName;
        public final LaborInfo labor;

        public LaborRecord(String projectName, LaborInfo labor) {
            this.projectName = projectName;
            this.labor = labor;
        }
    }
}
/**
 * A discount that is either a percentage off or a flat dollar amount off.
 */
public class Discount {

    public enum Type { NONE, PERCENT, FIXED }

    private Type type;
    private double amount; // percent (e.g. 10 = 10%) or dollars

    public Discount(Type type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public static Discount none() {
        return new Discount(Type.NONE, 0);
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }

    /**
     * Returns how many dollars come off the subtotal.
     * PERCENT: subtotal x (amount / 100)
     * FIXED:   amount (but never more than the subtotal)
     * NONE:    0
     */
   public double calculateDiscount(double subtotal) {
    if (type == Type.PERCENT) {
        return subtotal * (amount / 100);
    } else if (type == Type.FIXED) {
        return Math.min(amount, subtotal);
    } else {
        return 0;
    }
}
}
/**
 * Does all the math for one job and prints the results.
 */
public class Estimate {

    private static final double CUBIC_FEET_PER_YARD = 27.0;
    private static final double INCHES_PER_FOOT = 12.0;

    private Project project;
    private SlabType slabType;
    private double lengthFt;
    private double widthFt;
    private double thicknessIn;
    private LaborInfo labor;
    private double pricePerYard;
    private double wastePercent;
    private Discount discount;

    public Estimate(Project project, SlabType slabType, double lengthFt, double widthFt,
                    double thicknessIn, LaborInfo labor, double pricePerYard,
                    double wastePercent, Discount discount) {
        this.project = project;
        this.slabType = slabType;
        this.lengthFt = lengthFt;
        this.widthFt = widthFt;
        this.thicknessIn = thicknessIn;
        this.labor = labor;
        this.pricePerYard = pricePerYard;
        this.wastePercent = wastePercent;
        this.discount = discount;
    }

    /** area (sq ft) = length x width */
    public double getArea() {
        return lengthFt * widthFt;
    }

    /** cubic yards = (area x (thickness / 12)) / 27 */
    public double getCubicYards() {
        return (getArea() * (thicknessIn / INCHES_PER_FOOT)) / CUBIC_FEET_PER_YARD;
    }

    /** yards with waste = cubic yards x (1 + waste% / 100) */
    public double getCubicYardsWithWaste() {
        return getCubicYards() * (1 + wastePercent / 100);
    }

    /** material cost = yards with waste x price per yard */
    public double getMaterialCost() {
        return getCubicYardsWithWaste() * pricePerYard;
    }

    public double getLaborCost() {
        return labor.getLaborCost();
    }

    /** subtotal = material + labor */
    public double getSubtotal() {
        return getMaterialCost() + getLaborCost();
    }

    public double getDiscountAmount() {
        return discount.calculateDiscount(getSubtotal());
    }

    /** total = subtotal - discount */
    public double getTotal() {
        return getSubtotal() - getDiscountAmount();
    }

    /** Prints a clean, readable summary. */
    public void printSummary() {
        System.out.println();
        System.out.println("========== ESTIMATE ==========");
        System.out.println("Project:  " + project.getName());
        System.out.println("Location: " + project.getLocation());
        System.out.println("Client:   " + project.getClient());
        System.out.println("Slab:     " + slabType.getDisplayName());
        System.out.println("------------------------------");
        System.out.printf("Dimensions:        %.1f ft x %.1f ft x %.1f in%n", lengthFt, widthFt, thicknessIn);
        System.out.printf("Area:              %,.2f sq ft%n", getArea());
        System.out.printf("Concrete:          %,.2f cu yd%n", getCubicYards());
        System.out.printf("With %.0f%% waste:    %,.2f cu yd%n", wastePercent, getCubicYardsWithWaste());
        System.out.println("------------------------------");
        System.out.printf("Crew:              %d workers x %.1f hrs%n", labor.getWorkers(), labor.getHoursPerWorker());
        System.out.printf("Total labor hours: %,.1f%n", labor.getTotalHours());
        System.out.println("------------------------------");
        System.out.printf("Material cost:     $%,.2f%n", getMaterialCost());
        System.out.printf("Labor cost:        $%,.2f%n", getLaborCost());
        System.out.printf("Subtotal:          $%,.2f%n", getSubtotal());
        System.out.printf("Discount:         -$%,.2f%n", getDiscountAmount());
        System.out.printf("TOTAL:             $%,.2f%n", getTotal());
        System.out.println("==============================");
    }
}
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
projectName,workers,hoursPerWorker,hourlyRate
Brandon's Warehouse,5,8.0,25.0
Brandon's Warehouse,5,8.0,25.0

/**
 * Crew size, hours, and pay rate. This is what gets saved to the CSV.
 */
public class LaborInfo {

    private int workers;
    private double hoursPerWorker;
    private double hourlyRate;

    public LaborInfo(int workers, double hoursPerWorker, double hourlyRate) {
        this.workers = workers;
        this.hoursPerWorker = hoursPerWorker;
        this.hourlyRate = hourlyRate;
    }

    public int getWorkers() { return workers; }
    public double getHoursPerWorker() { return hoursPerWorker; }
    public double getHourlyRate() { return hourlyRate; }

    /** total labor hours = workers x hours per worker */
    public double getTotalHours() {
        // TODO
        return workers * hoursPerWorker;
    }

    /** labor cost = total hours x hourly rate */
    public double getLaborCost() {
        // TODO
        return getTotalHours() * hourlyRate;
    }
}

