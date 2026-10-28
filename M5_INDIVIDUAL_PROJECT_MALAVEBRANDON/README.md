# Concrete Pad Estimator

A Java console application that generates cost estimates for concrete pad construction jobs: materials, labor, waste, discounts, and a final total. Labor data is saved to and loaded from a CSV file so past jobs can be reviewed later.

## Background

My friend Jim inherited his uncle's construction business, which builds large warehouse facilities. He knows construction, but estimating concrete jobs (materials, labor, and costs) was confusing and slow. This program walks him through each job step by step and does the math for him.

## Features

**Core**
- Enter project details: name, location, and client
- Calculate the work area (length × width, in square feet)
- Calculate the concrete volume in cubic yards based on slab thickness
- Estimate labor from the number of workers, hours per worker, and hourly rate
- Calculate material cost, labor cost, and total project cost
- Save and load labor data (manpower and work hours) with a CSV file

**Extras**
- Preset slab types (residential pad, driveway, warehouse slab, heavy industrial) with typical thicknesses, plus a custom option
- Waste percentage added to the concrete order
- Discounts as either a percentage or a fixed dollar amount
- Input validation, so typing a letter where a number belongs doesn't crash the program

## How It's Organized

I think about this project as a military unit, where each class has one job:

| Class | Role | What it does |
|---|---|---|
| `Main` | Commanding officer | Starts the program, runs the menu, and gives the orders for when every other class does its job |
| `Project` | Mission briefing | Holds the job's name, location, and client |
| `InputHelper` | Gate guard | Checks everything the user types and asks again if something doesn't make sense |
| `Estimate` | Logistics officer | Does all the math and prints the final breakdown |
| `LaborInfo` | Manpower report | Workers, hours, and pay rate; calculates labor hours and labor cost |
| `SlabType` | Standard loadouts | Preset slab types with default thicknesses |
| `Discount` | Budget cut | Applies a percentage or fixed-dollar discount |
| `CsvManager` | Records clerk | Saves each job's labor data to `labor_data.csv` and reads it back |

`Main` is at the top, and everything reports to it. `LaborInfo`, `SlabType`, and `Discount` are helpers that feed into `Estimate`.

```
                    Main
     ┌───────┬───────┼──────────┐
 InputHelper Project Estimate  CsvManager
                       │
          ┌────────────┼──────────┐
      LaborInfo    Discount    SlabType
```

## Formulas

| Calculation | Formula |
|---|---|
| Area | length × width |
| Cubic yards | (area × thickness ÷ 12) ÷ 27 |
| Yards with waste | cubic yards × (1 + waste% ÷ 100) |
| Material cost | yards with waste × price per yard |
| Labor hours | workers × hours per worker |
| Labor cost | labor hours × hourly rate |
| Subtotal | material cost + labor cost |
| Total | subtotal − discount |

## How to Run

You need the Java JDK (version 14 or newer).

```
cd src
javac *.java
java Main
```

In VS Code, you can also open `Main.java` and click the Run button.

## Example

| Input | Value |
|---|---|
| Slab type | Warehouse slab (6 in) |
| Size | 100 ft × 50 ft |
| Crew | 5 workers × 8 hours at $25/hr |
| Concrete price | $150 per cubic yard |
| Waste | 10% |
| Discount | 10% off |

Output:

```
Area:              5,000.00 sq ft
Concrete:          92.59 cu yd
With 10% waste:    101.85 cu yd
Total labor hours: 40.0
Material cost:     $15,277.78
Labor cost:        $1,000.00
Subtotal:          $16,277.78
Discount:         -$1,627.78
TOTAL:             $14,650.00
```

## CSV Format

Each saved job is one line in `labor_data.csv`:

```
projectName,workers,hoursPerWorker,hourlyRate
Brandon's Warehouse,5,8.0,25.0
```

New jobs are added to the end of the file, so older jobs are never erased.

## Future Improvements

- Load a saved job and re-run its estimate (menu option 3)
- Save the full estimate to the CSV, not just the labor data
- A Swing GUI

## Author

Brandon Malave, IT and Computer Programming, Fayetteville Technical Community College
