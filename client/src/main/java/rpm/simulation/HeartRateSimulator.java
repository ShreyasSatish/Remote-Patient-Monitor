package rpm.simulation;

import java.time.Instant;
import java.util.Objects;
import java.util.Random;

/**
 * Simulates heart rate (beats per minute) using a random-walk model around a baseline.
 */
public class HeartRateSimulator implements VitalSimulator {
    private final Random random;
    private final double baselineBpm;
    private final double minBpm;
    private final double maxBpm;

    private double currentBpm;

    public HeartRateSimulator() {
        this(75.0, 60.0, 100.0, new Random());
    }

    public HeartRateSimulator(double baselineBpm, double minBpm, double maxBpm) {
        this(baselineBpm, minBpm, maxBpm, new Random());
    }

    public HeartRateSimulator(double baselineBpm, double minBpm, double maxBpm, Random random) {
        this.random = Objects.requireNonNull(random, "random");
        this.baselineBpm = baselineBpm;
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
        this.currentBpm = baselineBpm;
    }

    @Override
    public double nextValue(Instant time) {
        // Step heart rate value

        double randomStep = random.nextGaussian() * 1.5;
        // Pull heart rate slightly back to 'normal' value
        double pullToBaseline = (baselineBpm - currentBpm) * 0.05;

        currentBpm = currentBpm + randomStep + pullToBaseline;

        // Change min and max values
        if (currentBpm < minBpm) {
            currentBpm = minBpm;
        } else if (currentBpm > maxBpm) {
            currentBpm = maxBpm;
        }

        return currentBpm;
    }
}
