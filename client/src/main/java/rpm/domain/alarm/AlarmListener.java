package rpm.domain.alarm;

import rpm.domain.PatientId;

import java.time.Instant;

public interface AlarmListener {
    void onAlarmTransition(AlarmTransition transition);
    void onAlarmState(PatientId id, Instant time, AlarmState state);
}
