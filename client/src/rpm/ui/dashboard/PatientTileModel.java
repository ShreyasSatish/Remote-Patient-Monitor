package rpm.ui.dashboard;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;

import java.util.Map;

public final class PatientTileModel {
    public final PatientId id;
    public final String displayName;

    public final double hr;
    public final double rr;
    public final double sys;
    public final double dia;
    public final double temp;

    private PatientTileModel(PatientId id, String displayName,
                             double hr, double rr, double sys, double dia, double temp) {
        this.id = id;
        this.displayName = displayName;
        this.hr = hr;
        this.rr = rr;
        this.sys = sys;
        this.dia = dia;
        this.temp = temp;
    }

    public static PatientTileModel from(PatientId id, String name, VitalSnapshot snap) {
        if (snap == null) {
            return new PatientTileModel(id, name, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        }
        Map<VitalType, Double> v = snap.getValues();
        return new PatientTileModel(
                id, name,
                get(v, VitalType.HEART_RATE),
                get(v, VitalType.RESP_RATE),
                get(v, VitalType.BP_SYSTOLIC),
                get(v, VitalType.BP_DIASTOLIC),
                get(v, VitalType.TEMPERATURE)
        );
    }

    private static double get(Map<VitalType, Double> m, VitalType t) {
        Double x = m.get(t);
        return x == null ? Double.NaN : x;
    }
}
