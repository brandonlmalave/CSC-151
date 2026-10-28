/**
 * Basic info about the job. No math lives here.
 */
public class Project {

    private String name;
    private String location;
    private String client;

    public Project(String name, String location, String client) {
        this.name = name;
        this.location = location;
        this.client = client;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getClient() { return client; }
}
