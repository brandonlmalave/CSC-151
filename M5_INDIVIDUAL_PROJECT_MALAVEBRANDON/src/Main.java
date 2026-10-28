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