package rpm.domain.report;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.alarm.AlarmTransition;

import java.time.Instant;
import java.util.List;


public interface PatientDataSource {
    List<VitalSnapshot> getVitals(PatientId id, Instant from, Instant to);
    List<AlarmTransition> getAlarmTransitions(PatientId id, Instant from, Instant to);
}
