/**
 * Preset slab types (extra feature). Each one has a typical thickness.
 * Adjust the numbers to whatever your instructor / Jim wants.
 */
public enum SlabType {
    RESIDENTIAL_PAD("Residential pad", 4.0),
    DRIVEWAY("Driveway", 5.0),
    WAREHOUSE_SLAB("Warehouse slab", 6.0),
    HEAVY_INDUSTRIAL("Heavy industrial", 8.0),
    CUSTOM("Custom", 0.0); // user enters their own thickness

    private final String displayName;
    private final double defaultThicknessInches;

    SlabType(String displayName, double defaultThicknessInches) {
        this.displayName = displayName;
        this.defaultThicknessInches = defaultThicknessInches;
    }

    public String getDisplayName() { return displayName; }
    public double getDefaultThicknessInches() { return defaultThicknessInches; }
}
