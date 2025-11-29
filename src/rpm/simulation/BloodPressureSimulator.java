package rpm.simulation;

import java.time.Instant;
import java.util.Random;

/**
 * Simulates arterial blood pressure for a normal adult.
 * The simulator tracks systolic and diastolic pressure internally.
 */
public class BloodPressureSimulator implements VitalSimulator {

    private final Random random;
    private final double baselineSystolic;
    private final double baselineDiastolic;
    private final double minSystolic;
    private final double maxSystolic;

    private double currentSystolic;
    private double currentDiastolic;

    public BloodPressureSimulator() {
        this(120.0, 80.0, 90.0, 140.0);
    }

    public BloodPressureSimulator(double baselineSystolic,
                                  double baselineDiastolic,
                                  double minSystolic,
                                  double maxSystolic) {
        this.random = new Random();
        this.baselineSystolic = baselineSystolic;
        this.baselineDiastolic = baselineDiastolic;
        this.minSystolic = minSystolic;
        this.maxSystolic = maxSystolic;
        this.currentSystolic = baselineSystolic;
        this.currentDiastolic = baselineDiastolic;
    }

    @Override
    public double nextValue(Instant time) {
        double randomStep = random.nextGaussian() * 2.0; // ~ +/- 4 mmHg
        double pullToBaseline = (baselineSystolic - currentSystolic) * 0.05;

        currentSystolic = currentSystolic + randomStep + pullToBaseline;

        if (currentSystolic < minSystolic) {
            currentSystolic = minSystolic;
        } else if (currentSystolic > maxSystolic) {
            currentSystolic = maxSystolic;
        }

        // Diastolic follows systolic but with smaller excursions.
        double targetDiastolic =
                baselineDiastolic + (currentSystolic - baselineSystolic) * 0.5;
        double diastolicNoise = random.nextGaussian() * 1.5;

        currentDiastolic = targetDiastolic + diastolicNoise;

        return currentSystolic;
    }

    public double getCurrentSystolic() {
        return currentSystolic;
    }

    public double getCurrentDiastolic() {
        return currentDiastolic;
    }
}
