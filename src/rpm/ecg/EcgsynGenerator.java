package rpm.ecg;

/**
 * Adapter that will use the external ECGSYN implementation to generate ECG samples.
 */

public class EcgsynGenerator implements EcgGenerator {

    @Override
    public double[] generateSegment(double durationSeconds,
                                    double meanHeartRateBpm,
                                    double samplingFrequencyHz) {

        // TODO: call ECGSYN code and return the generated ECG samples
        return new double[0];
    }
}
