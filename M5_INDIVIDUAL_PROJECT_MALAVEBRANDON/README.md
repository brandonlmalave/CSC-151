# Concrete Pad Estimator: Skeleton

Every class compiles and runs. Anything marked `// TODO` is yours to fill in.

## Files

| File | Job |
|---|---|
| `Main.java` | Menu loop. Asks the questions and wires everything together |
| `Project.java` | Name, location, client. Just data |
| `SlabType.java` | Preset slab types with default thickness (extra feature) |
| `LaborInfo.java` | Workers, hours, rate, plus labor hours and cost math |
| `Estimate.java` | Area, cubic yards, waste, material cost, totals, printed summary |
| `Discount.java` | Percent or fixed discount (extra feature) |
| `CsvManager.java` | Save and load labor data to `labor_data.csv` |
| `InputHelper.java` | Safe input so bad typing doesn't crash the program |

## Formulas

- Area = length × width
- Cubic yards = (area × thickness ÷ 12) ÷ 27
- Yards with waste = yards × (1 + waste% ÷ 100)
- Material cost = yards with waste × price per yard
- Labor hours = workers × hours per worker
- Labor cost = labor hours × hourly rate
- Subtotal = material + labor
- Total = subtotal − discount

## Suggested order

1. Fill in the math in `LaborInfo`, `Estimate`, and `Discount`.
2. Finish `InputHelper`.
3. Replace the placeholder values in `Main.createNewEstimate` with real input.
4. Finish `printSummary`.
5. Finish `CsvManager` save and load, then `viewSavedLabor`.
6. Stretch goal: menu option 3 (load a saved job).

Sanity check: 100 ft × 50 ft × 6 in = 5,000 sq ft, which is about 92.59 cubic yards.

## Compile and run

```
cd src
javac *.java
java Main
```
