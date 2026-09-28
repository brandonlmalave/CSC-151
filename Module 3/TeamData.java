import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// Written by: Brandon Malave – File I/O for the project: reads team data from CSV files and writes it back.
public class TeamData {

    // Written by: Brandon Malave – Reads a CSV file and returns each data row as an array of fields.
    public static List<String[]> readCsv(String fileName) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // first line is the header, not data
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                rows.add(fields);
            }
        }
        return rows;
    }

    // Written by: Brandon Malave – Writes a header plus one line per team member, replacing the old file.
    public static void writeCsv(String fileName, String header, List<? extends TeamMember> members) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println(header);
            for (TeamMember member : members) {
                writer.println(member.toCsvRow());
            }
        }
    }
}
