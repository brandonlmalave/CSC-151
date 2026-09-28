import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String PLAYERS_FILE = "players.csv";
    private static final String COACHES_FILE = "coaches.csv";
    private static final String STAFF_FILE = "staff.csv";

    // Written by: Brandon Malave – Loads the roster from CSV files, runs the menu, and saves on exit.
    public static void main(String[] args) {
        ArrayList<Player> players = loadPlayers();
        ArrayList<Coach> coaches = loadCoaches();
        ArrayList<StaffMember> staff = loadStaff();

        boolean running = true;
        while (running) {
            String[] options = {"View Players", "View Coaches", "View Staff",
                                "Add Player", "Add Coach", "Add Staff", "Save & Exit"};
            int choice = JOptionPane.showOptionDialog(
                null, "Chicago Bears — Team Management",
                "Main Menu", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]
            );

            switch (choice) {
                case 0 -> showList("Players", players);
                case 1 -> showList("Coaches", coaches);
                case 2 -> showList("Staff", staff);
                case 3 -> addPlayer(players);
                case 4 -> addCoach(coaches);
                case 5 -> addStaff(staff);
                default -> {
                    saveAll(players, coaches, staff);
                    running = false;
                }
            }
        }
    }

    // Written by: Brandon Malave – Builds Player objects from players.csv (name, position, jerseyNumber).
    private static ArrayList<Player> loadPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (String[] row : readRows(PLAYERS_FILE, 3)) {
            try {
                players.add(new Player(row[0], row[1], Integer.parseInt(row[2])));
            } catch (NumberFormatException e) {
                // a row with a non-numeric jersey is skipped so one bad line doesn't stop the program
            }
        }
        return players;
    }

    // Written by: Brandon Malave – Builds Coach objects from coaches.csv (name, title).
    private static ArrayList<Coach> loadCoaches() {
        ArrayList<Coach> coaches = new ArrayList<>();
        for (String[] row : readRows(COACHES_FILE, 2)) {
            coaches.add(new Coach(row[0], row[1]));
        }
        return coaches;
    }

    // Written by: Brandon Malave – Builds StaffMember objects from staff.csv (name, department).
    private static ArrayList<StaffMember> loadStaff() {
        ArrayList<StaffMember> staff = new ArrayList<>();
        for (String[] row : readRows(STAFF_FILE, 2)) {
            staff.add(new StaffMember(row[0], row[1]));
        }
        return staff;
    }

    // Written by: Brandon Malave – Reads a file through TeamData and keeps only rows with enough columns.
    private static List<String[]> readRows(String fileName, int columns) {
        List<String[]> valid = new ArrayList<>();
        try {
            for (String[] row : TeamData.readCsv(fileName)) {
                if (row.length >= columns) {
                    valid.add(row);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Could not read " + fileName + ". Starting with an empty list.",
                "File Error", JOptionPane.WARNING_MESSAGE);
        }
        return valid;
    }

    // Written by: Brandon Malave – Prompts for player details with showInputDialog and validates the jersey number.
    private static void addPlayer(ArrayList<Player> players) {
        String name = ask("Player name:");
        if (name == null) return;
        String position = ask("Position (e.g. QB, WR, CB):");
        if (position == null) return;
        String numberText = ask("Jersey number (0-99):");
        if (numberText == null) return;

        try {
            int number = Integer.parseInt(numberText);
            if (number < 0 || number > 99) {
                JOptionPane.showMessageDialog(null, "Jersey number must be between 0 and 99.");
                return;
            }
            Player player = new Player(name, position.toUpperCase(), number);
            players.add(player);
            JOptionPane.showMessageDialog(null, "Added: " + player);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "\"" + numberText + "\" is not a valid jersey number.");
        }
    }

    // Written by: Brandon Malave – Prompts for a coach's name and title.
    private static void addCoach(ArrayList<Coach> coaches) {
        String name = ask("Coach name:");
        if (name == null) return;
        String title = ask("Title (e.g. Offensive Coordinator):");
        if (title == null) return;

        Coach coach = new Coach(name, title);
        coaches.add(coach);
        JOptionPane.showMessageDialog(null, "Added: " + coach);
    }

    // Written by: Brandon Malave – Prompts for a staff member's name and department.
    private static void addStaff(ArrayList<StaffMember> staff) {
        String name = ask("Staff member name:");
        if (name == null) return;
        String department = ask("Department (e.g. Athletic Trainer):");
        if (department == null) return;

        StaffMember member = new StaffMember(name, department);
        staff.add(member);
        JOptionPane.showMessageDialog(null, "Added: " + member);
    }

    // Written by: Brandon Malave – Shared input prompt; returns null if the user cancels or leaves it blank.
    private static String ask(String prompt) {
        String input = JOptionPane.showInputDialog(null, prompt);
        if (input == null) {
            return null;
        }
        // commas would split one value into two columns when the CSV is read back
        input = input.replace(",", "").trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(null, "That field can't be blank.");
            return null;
        }
        return input;
    }

    // Written by: Brandon Malave – Writes all three lists back to their CSV files.
    private static void saveAll(List<Player> players, List<Coach> coaches, List<StaffMember> staff) {
        try {
            TeamData.writeCsv(PLAYERS_FILE, "name,position,jerseyNumber", players);
            TeamData.writeCsv(COACHES_FILE, "name,title", coaches);
            TeamData.writeCsv(STAFF_FILE, "name,department", staff);
            JOptionPane.showMessageDialog(null, "Roster saved to " + PLAYERS_FILE + ", "
                + COACHES_FILE + ", and " + STAFF_FILE + ".");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save failed: " + e.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Original method from Module 2, updated by Brandon Malave to add a title and an empty-list message.
    private static void showList(String title, List<? extends TeamMember> list) {
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No " + title.toLowerCase() + " yet.", title, JOptionPane.PLAIN_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (TeamMember member : list) {
            sb.append(member).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), title, JOptionPane.PLAIN_MESSAGE);
    }
}
