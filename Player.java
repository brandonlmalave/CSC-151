public class Player extends TeamMember {
    private String position;
    private int jerseyNumber;

    public Player(String name, String position, int jerseyNumber) {
        super(name);
        this.position = position;
        this.jerseyNumber = jerseyNumber;
    }

    @Override
    public String toString() {
        return getName() + " - #" + jerseyNumber + " - " + position;
    }
}