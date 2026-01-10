package rpm.ui.app;

import java.time.Duration;

public enum AlertDuration {
    SEC_10,
    SEC_30,
    MIN_1,
    UNTIL_RESOLVED;

    public Duration toDurationOrNull() {
        switch (this) {
            case SEC_10: return Duration.ofSeconds(10);
            case SEC_30: return Duration.ofSeconds(30);
            case MIN_1:  return Duration.ofMinutes(1);
            case UNTIL_RESOLVED:
            default:     return null;
        }
    }
}
