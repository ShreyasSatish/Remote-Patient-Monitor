package rpm.domain.alarm;

public final class ThresholdBand {
    public final double lowAmber;
    public final double lowRed;
    public final double highAmber;
    public final double highRed;

    // ai says - if you only care about high-side or low-side, set unused to +/-INF
    public ThresholdBand(double lowAmber, double lowRed, double highAmber, double highRed) {
        this.lowAmber = lowAmber;
        this.lowRed = lowRed;
        this.highAmber = highAmber;
        this.highRed = highRed;
    }
}
