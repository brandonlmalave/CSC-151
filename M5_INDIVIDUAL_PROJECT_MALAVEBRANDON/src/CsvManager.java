
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