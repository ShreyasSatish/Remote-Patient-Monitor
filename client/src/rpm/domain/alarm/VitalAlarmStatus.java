package rpm.domain.alarm;

import rpm.domain.VitalType;

import java.time.Instant;

public final class VitalAlarmStatus {
    private final VitalType type;
    private final AlarmLevel level;
    private final String reason;
    private final Instant since;

    public VitalAlarmStatus(VitalType type, AlarmLevel level, String reason, Instant since) {
        this.type = type;
        this.level = level;
        this.reason = reason;
        this.since = since;
    }

    // shreyas: these will be helpful used when UI is set up
    // - sohani

    public VitalType getType() { return type; }
    public AlarmLevel getLevel() { return level; }
    public String getReason() { return reason; }
    public Instant getSince() { return since; }
}
