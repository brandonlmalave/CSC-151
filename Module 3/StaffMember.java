// Written by: Brandon Malave – Staff class with a department.
public class StaffMember extends TeamMember {
    private String department;

    public StaffMember(String name, String department) {
        super(name);
        this.department = department;
    }

    // Written by: Brandon Malave – Column order must match the header in staff.csv.
    @Override
    public String toCsvRow() {
        return getName() + "," + department;
    }

    @Override
    public String toString() {
        return getName() + " - " + department;
    }
}
