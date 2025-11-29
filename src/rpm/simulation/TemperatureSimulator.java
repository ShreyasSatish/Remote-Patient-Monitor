package rpm.simulation;

import java.time.Instant;
import java.util.Random;

/**
 * Simulates core body temperature (degrees Celsius) for a normal adult.
 * Values stay close to a baseline with very slow variation.
 */
public class TemperatureSimulator implements VitalSimulator {

    private final Random random;
    private final double baselineTempC;
    private final double minTempC;
    private final double maxTempC;

    private double currentTempC;

    public TemperatureSimulator() {
        this(36.8, 36.5, 37.5);
    }

    public TemperatureSimulator(double baselineTempC,
                                double minTempC,
                                double maxTempC) {
        this.random = new Random();
        this.baselineTempC = baselineTempC;
        this.minTempC = minTempC;
        this.maxTempC = maxTempC;
        this.currentTempC = baselineTempC;
    }

    @Override
    public double nextValue(Instant time) {
        double randomStep = random.nextGaussian() * 0.02; // ~ +/- 0.04 °C
        double pullToBaseline = (baselineTempC - currentTempC) * 0.02;

        currentTempC = currentTempC + randomStep + pullToBaseline;

        if (currentTempC < minTempC) {
            currentTempC = minTempC;
        } else if (currentTempC > maxTempC) {
            currentTempC = maxTempC;
        }

        return currentTempC;
    }
}
