/**
 * Crew size, hours, and pay rate. This is what gets saved to the CSV.
 */
public class LaborInfo {

    private int workers;
    private double hoursPerWorker;
    private double hourlyRate;

    public LaborInfo(int workers, double hoursPerWorker, double hourlyRate) {
        this.workers = workers;
        this.hoursPerWorker = hoursPerWorker;
        this.hourlyRate = hourlyRate;
    }

    public int getWorkers() { return workers; }
    public double getHoursPerWorker() { return hoursPerWorker; }
    public double getHourlyRate() { return hourlyRate; }

    /** total labor hours = workers x hours per worker */
    public double getTotalHours() {
        return workers * hoursPerWorker;
    }

    /** labor cost = total hours x hourly rate */
    public double getLaborCost() {
        return getTotalHours() * hourlyRate;
    }
}
