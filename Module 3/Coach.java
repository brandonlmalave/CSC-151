// Written by: Brandon Malave – Coach class with a coaching title.
public class Coach extends TeamMember {
    private String title;

    public Coach(String name, String title) {
        super(name);
        this.title = title;
    }

    // Written by: Brandon Malave – Column order must match the header in coaches.csv.
    @Override
    public String toCsvRow() {
        return getName() + "," + title;
    }

    @Override
    public String toString() {
        return getName() + " - " + title;
    }
}
