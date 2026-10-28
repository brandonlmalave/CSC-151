/**
 * Does all the math for one job and prints the results.
 */
public class Estimate {

    private static final double CUBIC_FEET_PER_YARD = 27.0;
    private static final double INCHES_PER_FOOT = 12.0;

    private Project project;
    private SlabType slabType;
    private double lengthFt;
    private double widthFt;
    private double thicknessIn;
    private LaborInfo labor;
    private double pricePerYard;
    private double wastePercent;
    private Discount discount;

    public Estimate(Project project, SlabType slabType, double lengthFt, double widthFt,
                    double thicknessIn, LaborInfo labor, double pricePerYard,
                    double wastePercent, Discount discount) {
        this.project = project;
        this.slabType = slabType;
        this.lengthFt = lengthFt;
        this.widthFt = widthFt;
        this.thicknessIn = thicknessIn;
        this.labor = labor;
        this.pricePerYard = pricePerYard;
        this.wastePercent = wastePercent;
        this.discount = discount;
    }

    /** area (sq ft) = length x width */
    public double getArea() {
        return lengthFt * widthFt;
    }

    /** cubic yards = (area x (thickness / 12)) / 27 */
    public double getCubicYards() {
        return (getArea() * (thicknessIn / INCHES_PER_FOOT)) / CUBIC_FEET_PER_YARD;
    }

    /** yards with waste = cubic yards x (1 + waste% / 100) */
    public double getCubicYardsWithWaste() {
        return getCubicYards() * (1 + wastePercent / 100);
    }

    /** material cost = yards with waste x price per yard */
    public double getMaterialCost() {
        return getCubicYardsWithWaste() * pricePerYard;
    }

    public double getLaborCost() {
        return labor.getLaborCost();
    }

    /** subtotal = material + labor */
    public double getSubtotal() {
        return getMaterialCost() + getLaborCost();
    }

    public double getDiscountAmount() {
        return discount.calculateDiscount(getSubtotal());
    }

    /** total = subtotal - discount */
    public double getTotal() {
        return getSubtotal() - getDiscountAmount();
    }

    /** Prints a clean, readable summary. */
    public void printSummary() {
        System.out.println();
        System.out.println("========== ESTIMATE ==========");
        System.out.println("Project:  " + project.getName());
        System.out.println("Location: " + project.getLocation());
        System.out.println("Client:   " + project.getClient());
        System.out.println("Slab:     " + slabType.getDisplayName());
        System.out.println("------------------------------");
        System.out.printf("Dimensions:        %.1f ft x %.1f ft x %.1f in%n", lengthFt, widthFt, thicknessIn);
        System.out.printf("Area:              %,.2f sq ft%n", getArea());
        System.out.printf("Concrete:          %,.2f cu yd%n", getCubicYards());
        System.out.printf("With %.0f%% waste:    %,.2f cu yd%n", wastePercent, getCubicYardsWithWaste());
        System.out.println("------------------------------");
        System.out.printf("Crew:              %d workers x %.1f hrs%n", labor.getWorkers(), labor.getHoursPerWorker());
        System.out.printf("Total labor hours: %,.1f%n", labor.getTotalHours());
        System.out.println("------------------------------");
        System.out.printf("Material cost:     $%,.2f%n", getMaterialCost());
        System.out.printf("Labor cost:        $%,.2f%n", getLaborCost());
        System.out.printf("Subtotal:          $%,.2f%n", getSubtotal());
        System.out.printf("Discount:         -$%,.2f%n", getDiscountAmount());
        System.out.printf("TOTAL:             $%,.2f%n", getTotal());
        System.out.println("==============================");
    }
}