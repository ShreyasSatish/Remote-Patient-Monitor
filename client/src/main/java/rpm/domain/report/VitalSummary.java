package rpm.domain.report;

public final class VitalSummary {
    private final int n;
    private final double min;
    private final double max;
    private final double mean;

    public VitalSummary(int n, double min, double max, double mean) {
        this.n = n;
        this.min = min;
        this.max = max;
        this.mean = mean;
    }

    public int getN() { return n; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getMean() { return mean; }
}
