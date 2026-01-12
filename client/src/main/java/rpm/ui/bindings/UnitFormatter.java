package rpm.ui.bindings;

import rpm.domain.VitalType;

public final class UnitFormatter {
    public static String unit(VitalType t) {
        switch (t) {
            case HEART_RATE: return "bpm";
            case RESP_RATE: return "br/min";
            case BP_SYSTOLIC:
            case BP_DIASTOLIC: return "mmHg";
            case TEMPERATURE: return "°C";
            default: return "";
        }
    }

    private UnitFormatter() {}
}
