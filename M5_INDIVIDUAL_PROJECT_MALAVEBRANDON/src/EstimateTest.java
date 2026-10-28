import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the estimator's math.
 * Each test builds a known job and checks one answer we already worked out by hand.
 */
public class EstimateTest {

    // How close a decimal answer has to be to count as correct (half a cent)
    private static final double DELTA = 0.005;

    /** The warehouse example from the demo: 100 x 50 ft, 6 in, 5 workers x 8 hrs @ $25, $150/yd, 10% waste. */
    private Estimate warehouseJob(Discount discount) {
        Project project = new Project("Test Job", "Fayetteville, NC", "Jim");
        LaborInfo labor = new LaborInfo(5, 8, 25);
        return new Estimate(project, SlabType.WAREHOUSE_SLAB, 100, 50, 6,
                labor, 150, 10, discount);
    }

    @Test
    void areaIsLengthTimesWidth() {
        assertEquals(5000.0, warehouseJob(Discount.none()).getArea(), DELTA);
    }

    @Test
    void cubicYardsUseThicknessInFeet() {
        // 5,000 sq ft x 0.5 ft = 2,500 cu ft, divided by 27 = 92.59 cu yd
        assertEquals(92.59, warehouseJob(Discount.none()).getCubicYards(), 0.01);
    }

    @Test
    void wasteAddsExtraConcrete() {
        // 92.59 x 1.10 = 101.85 cu yd
        assertEquals(101.85, warehouseJob(Discount.none()).getCubicYardsWithWaste(), 0.01);
    }

    @Test
    void laborHoursAndCost() {
        LaborInfo labor = new LaborInfo(5, 8, 25);
        assertEquals(40.0, labor.getTotalHours(), DELTA);
        assertEquals(1000.0, labor.getLaborCost(), DELTA);
    }

    @Test
    void totalWithNoDiscountIsMaterialPlusLabor() {
        Estimate job = warehouseJob(Discount.none());
        assertEquals(15277.78, job.getMaterialCost(), DELTA);
        assertEquals(16277.78, job.getTotal(), DELTA);
    }

    @Test
    void percentDiscountMatchesTheDemo() {
        // 10% off $16,277.78 = $1,627.78 off, total $14,650.00
        Estimate job = warehouseJob(new Discount(Discount.Type.PERCENT, 10));
        assertEquals(1627.78, job.getDiscountAmount(), DELTA);
        assertEquals(14650.00, job.getTotal(), DELTA);
    }

    @Test
    void fixedDiscountTakesOffExactDollars() {
        Estimate job = warehouseJob(new Discount(Discount.Type.FIXED, 500));
        assertEquals(15777.78, job.getTotal(), DELTA);
    }

    @Test
    void fixedDiscountNeverGoesBelowZero() {
        // A $1,000,000 discount on a $100 job should only take off $100
        Discount huge = new Discount(Discount.Type.FIXED, 1_000_000);
        assertEquals(100.0, huge.calculateDiscount(100), DELTA);
    }
}
