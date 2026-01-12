package rpm.domain.alarm;

import rpm.domain.VitalType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class AlarmState {
    private final AlarmLevel overall;
    private final Map<VitalType, VitalAlarmStatus> byVital;

    public AlarmState(AlarmLevel overall, Map<VitalType, VitalAlarmStatus> byVital) {
        this.overall = overall;
        this.byVital = Collections.unmodifiableMap(new EnumMap<>(byVital));
    }

    public AlarmLevel getOverall() { return overall; }
    public Map<VitalType, VitalAlarmStatus> getByVital() { return byVital; }
}
