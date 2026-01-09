package rpm.domain.alarm;

import rpm.domain.PatientId;
import rpm.domain.VitalType;

import java.time.Instant;

public final class AlarmTransition {
    private final PatientId patientId;
    private final VitalType vitalType;
    private final AlarmLevel from;
    private final AlarmLevel to;
    private final Instant time;
    private final String reason;

    public AlarmTransition(PatientId patientId, VitalType vitalType, AlarmLevel from, AlarmLevel to, Instant time, String reason) {
        this.patientId = patientId;
        this.vitalType = vitalType;
        this.from = from;
        this.to = to;
        this.time = time;
        this.reason = reason;
    }

    public PatientId getPatientId() { return patientId; }
    public VitalType getVitalType() { return vitalType; }
    public AlarmLevel getFrom() { return from; }
    public AlarmLevel getTo() { return to; }
    public Instant getTime() { return time; }
    public String getReason() { return reason; }
}
