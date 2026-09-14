import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Coach> coaches = new ArrayList<>();
        ArrayList<StaffMember> staff = new ArrayList<>();

        // Populate with Bears data
        players.add(new Player("Caleb Williams", "QB", 18));
        coaches.add(new Coach("Ben Johnson", "Head Coach"));
        staff.add(new StaffMember("Jane Doe", "Athletic Trainer"));

        // Simple menu loop using JOptionPane
        boolean running = true;
        while (running) {
            String[] options = {"View Players", "View Coaches", "View Staff", "Exit"};
            int choice = JOptionPane.showOptionDialog(
                null, "Chicago Bears — Team Management",
                "Main Menu", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]
            );

            switch (choice) {
                case 0 -> showList(players);
                case 1 -> showList(coaches);
                case 2 -> showList(staff);
                default -> running = false;
            }
        }
    }

    private static void showList(ArrayList<? extends TeamMember> list) {
        StringBuilder sb = new StringBuilder();
        for (TeamMember member : list) {
            sb.append(member.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
}