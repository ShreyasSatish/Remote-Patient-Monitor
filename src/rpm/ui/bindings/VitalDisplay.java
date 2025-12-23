package rpm.ui.bindings;

public final class VitalDisplay {
    public static String fmt1(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "--";
        return String.format("%.1f", v);
    }

    public static String fmt0(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "--";
        return String.format("%.0f", v);
    }

    private VitalDisplay() {}
}
