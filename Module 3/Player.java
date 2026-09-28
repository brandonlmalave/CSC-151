// Written by: Brandon Malave – Player class with position and jersey number.
public class Player extends TeamMember {
    private String position;
    private int jerseyNumber;

    public Player(String name, String position, int jerseyNumber) {
        super(name);
        this.position = position;
        this.jerseyNumber = jerseyNumber;
    }

    // Written by: Brandon Malave – Column order must match the header in players.csv.
    @Override
    public String toCsvRow() {
        return getName() + "," + position + "," + jerseyNumber;
    }

    @Override
    public String toString() {
        return getName() + " - #" + jerseyNumber + " - " + position;
    }
}
