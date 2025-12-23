package rpm.ui.alerts;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;

import java.util.Map;

public final class AlertTileModel {
    public final PatientId id;
    public final String name;
    public final double hr, rr, sys, dia, temp;
    public final boolean alerting;
    public final boolean acknowledged;

    private AlertTileModel(PatientId id, String name,
                           double hr, double rr, double sys, double dia, double temp,
                           boolean alerting, boolean acknowledged) {
        this.id = id;
        this.name = name;
        this.hr = hr;
        this.rr = rr;
        this.sys = sys;
        this.dia = dia;
        this.temp = temp;
        this.alerting = alerting;
        this.acknowledged = acknowledged;
    }

    public static AlertTileModel from(PatientId id, String name, VitalSnapshot snap,
                                      boolean alerting, boolean acknowledged) {
        Map<VitalType, Double> v = (snap == null) ? java.util.Collections.emptyMap() : snap.getValues();
        return new AlertTileModel(
                id, name,
                get(v, VitalType.HEART_RATE),
                get(v, VitalType.RESP_RATE),
                get(v, VitalType.BP_SYSTOLIC),
                get(v, VitalType.BP_DIASTOLIC),
                get(v, VitalType.TEMPERATURE),
                alerting, acknowledged
        );
    }

    private static double get(Map<VitalType, Double> m, VitalType t) {
        Double x = m.get(t);
        return x == null ? Double.NaN : x;
    }
}
