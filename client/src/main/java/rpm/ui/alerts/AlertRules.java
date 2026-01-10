package rpm.ui.alerts;

import rpm.domain.alarm.AlarmLevel;
import rpm.domain.alarm.AlarmState;
import rpm.ui.app.AlertDuration;
import rpm.ui.app.AppContext;

import java.time.Duration;

public final class AlertRules {

    public static boolean isAlertingRedOnly(AlarmState s) {
        if (s == null) return false;
        return s.getOverall() == AlarmLevel.RED;
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
                return Duration.ofHours(1);
                // potentially change this
        }
    }

    private AlertRules() {}
}
