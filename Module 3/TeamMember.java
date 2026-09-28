// Written by: Brandon Malave – Base class shared by every player, coach, and staff member.
public class TeamMember {
    private String name;

    public TeamMember(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Written by: Brandon Malave – Returns this member as one CSV line; subclasses add their own columns.
    public String toCsvRow() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
