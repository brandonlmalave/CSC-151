public class StaffMember extends TeamMember {
    private String department;

    public StaffMember(String name, String department) {
        super(name);
        this.department = department;
    }

    @Override
    public String toString() {
        return getName() + " - " + department;
    }
}