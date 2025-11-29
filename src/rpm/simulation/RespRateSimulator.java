package rpm.simulation;

import java.time.Instant;
import java.util.Random;

/**
 * Simulates respiratory rate (breaths per minute) for a normal resting adult.
 */
public class RespRateSimulator implements VitalSimulator {

    private final Random random;
    private final double baselineRespPerMin;
    private final double minRespPerMin;
    private final double maxRespPerMin;

    private double currentRespPerMin;

    public RespRateSimulator() {
        this(16.0, 12.0, 20.0);
    }

    public RespRateSimulator(double baselineRespPerMin,
                             double minRespPerMin,
                             double maxRespPerMin) {
        this.random = new Random();
        this.baselineRespPerMin = baselineRespPerMin;
        this.minRespPerMin = minRespPerMin;
        this.maxRespPerMin = maxRespPerMin;
        this.currentRespPerMin = baselineRespPerMin;
    }

    @Override
    public double nextValue(Instant time) {
        double randomStep = random.nextGaussian() * 0.4; // ~ +/- 1 breath/min
        double pullToBaseline = (baselineRespPerMin - currentRespPerMin) * 0.08;

        currentRespPerMin = currentRespPerMin + randomStep + pullToBaseline;

        if (currentRespPerMin < minRespPerMin) {
            currentRespPerMin = minRespPerMin;
        } else if (currentRespPerMin > maxRespPerMin) {
            currentRespPerMin = maxRespPerMin;
        }

        return currentRespPerMin;
    }
}
