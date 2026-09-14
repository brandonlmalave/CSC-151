public class Coach extends TeamMember {
    private String title;

    public Coach(String name, String title) {
        super(name);
        this.title = title;
    }

    @Override
    public String toString() {
        return getName() + " - " + title;
    }
}
