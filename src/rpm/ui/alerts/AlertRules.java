package rpm.ui.alerts;

import rpm.domain.alarm.AlarmLevel;
import rpm.domain.alarm.AlarmState;
import rpm.ui.app.AlertDuration;
import rpm.ui.app.AppContext;

import java.time.Duration;

public final class AlertRules {

    /** Red-only for now: consider any non-green as alerting (AMBER/RED). */
    public static boolean isAlertingRedOnly(AlarmState s) {
        if (s == null) return false;
        // If you truly want ONLY RED later, change to: return s.getOverall() == AlarmLevel.RED;
        return s.getOverall() != AlarmLevel.GREEN;
    }

    public static Duration resolveDuration(AppContext ctx) {
        AlertDuration d = ctx.settings.getAlertDuration();
        if (d == null) return Duration.ofSeconds(30);

        switch (d) {
            case SEC_10: return Duration.ofSeconds(10);
            case SEC_30: return Duration.ofSeconds(30);
            case MIN_1: return Duration.ofMinutes(1);
            case UNTIL_RESOLVED:
            default:
                // For “until resolved”, we can suppress for a long time
                // (or you can implement a checkbox popup later).
                return Duration.ofHours(24);
        }
    }

    private AlertRules() {}
}
