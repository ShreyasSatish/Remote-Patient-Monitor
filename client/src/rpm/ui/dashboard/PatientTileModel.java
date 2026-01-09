package rpm.ui.dashboard;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;

import java.util.Map;

public final class PatientTileModel {

    public final PatientId id;
    public final String displayName;
    public final boolean alerting;

    public final double hr;
    public final double rr;
    public final double sys;
    public final double dia;
    public final double temp;

    private PatientTileModel(PatientId id, String displayName,
                             double hr, double rr, double sys, double dia, double temp,
                             boolean alerting) {
        this.id = id;
        this.displayName = displayName;
        this.hr = hr;
        this.rr = rr;
        this.sys = sys;
        this.dia = dia;
        this.temp = temp;
        this.alerting = alerting;
    }

    public static PatientTileModel from(PatientId id, String name,
                                        VitalSnapshot snap, boolean alerting) {
        if (snap == null) {
            return new PatientTileModel(
                    id, name,
                    Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                    alerting
            );
        }

        Map<VitalType, Double> v = snap.getValues();
        return new PatientTileModel(
                id, name,
                get(v, VitalType.HEART_RATE),
                get(v, VitalType.RESP_RATE),
                get(v, VitalType.BP_SYSTOLIC),
                get(v, VitalType.BP_DIASTOLIC),
                get(v, VitalType.TEMPERATURE),
                alerting
        );
    }

    private static double get(Map<VitalType, Double> m, VitalType t) {
        Double x = m.get(t);
        return x == null ? Double.NaN : x;
    }
}
