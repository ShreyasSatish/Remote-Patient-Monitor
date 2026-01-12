package rpm.ui.alerts;

import rpm.domain.alarm.AlarmLevel;
import rpm.domain.alarm.AlarmState;
import rpm.ui.app.AlertDuration;
import rpm.ui.app.AppContext;

import java.time.Duration;

public final class AlertRules {

    public static boolean isAlertingRedOnly(AlarmState s) {
        return s != null && s.getOverall() == AlarmLevel.RED;
    }

    // Resolve hides alert via cooldown (even if still red)
    public static Duration resolveCooldown(AppContext ctx) {
        AlertDuration d = ctx.settings.getAlertDuration();
        if (d == null) return Duration.ofSeconds(30);

        switch (d) {
            case SEC_10: return Duration.ofSeconds(10);
            case SEC_30: return Duration.ofSeconds(30);
            case MIN_1:  return Duration.ofMinutes(1);
            case UNTIL_RESOLVED:
            default:
                return Duration.ofSeconds(45);
        }
    }

    private AlertRules() {}
}
