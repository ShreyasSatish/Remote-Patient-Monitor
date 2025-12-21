package rpm.simulation;

import rpm.domain.PatientId;

import java.time.Instant;

public final class PatientVitalsRow {
    private final PatientId patientId;
    private final Instant timestamp;
    private final double hr;
    private final double rr;
    private final double sys;
    private final double dia;
    private final double temp;

    public PatientVitalsRow(PatientId patientId,
                            Instant timestamp,
                            double hr,
                            double rr,
                            double sys,
                            double dia,
                            double temp) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.hr = hr;
        this.rr = rr;
        this.sys = sys;
        this.dia = dia;
        this.temp = temp;
    }

    public PatientId getPatientId() { return patientId; }
    public Instant getTimestamp() { return timestamp; }
    public double getHr() { return hr; }
    public double getRr() { return rr; }
    public double getSys() { return sys; }
    public double getDia() { return dia; }
    public double getTemp() { return temp; }
}


