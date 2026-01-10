package rpm.domain.report;

import rpm.domain.PatientId;
import rpm.domain.VitalType;
import rpm.domain.alarm.AlarmTransition;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class PatientReport {
    private final PatientId patientId;
    private final Instant from;
    private final Instant to;

    private final Map<VitalType, VitalSummary> summaries;
    private final List<AlarmTransition> alarmTransitions;

    public PatientReport(PatientId patientId,
                         Instant from,
                         Instant to,
                         Map<VitalType, VitalSummary> summaries,
                         List<AlarmTransition> alarmTransitions) {
        this.patientId = patientId;
        this.from = from;
        this.to = to;
        this.summaries = Collections.unmodifiableMap(new EnumMap<>(summaries));
        this.alarmTransitions = Collections.unmodifiableList(alarmTransitions);
    }

    public PatientId getPatientId() { return patientId; }
    public Instant getFrom() { return from; }
    public Instant getTo() { return to; }
    public Map<VitalType, VitalSummary> getSummaries() { return summaries; }
    public List<AlarmTransition> getAlarmTransitions() { return alarmTransitions; }
}
