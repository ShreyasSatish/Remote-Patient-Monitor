package rpm.ecg;

/**
 * Interface for an ECG waveform generator.
 */

public interface EcgGenerator {

    double[] generateSegment(double durationSeconds,
                             double meanHeartRateBpm,
                             double samplingFrequencyHz);
}

