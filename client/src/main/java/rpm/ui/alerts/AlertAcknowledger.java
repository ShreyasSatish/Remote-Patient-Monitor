package rpm.ui.alerts;

import rpm.domain.PatientId;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public final class AlertAcknowledger {

    private final Map<PatientId, Instant> ackUntil = new HashMap<>();

    public void acknowledge(PatientId id, Duration duration) {
        if (id == null) return;
        if (duration == null) duration = Duration.ofSeconds(30);
        if (duration.isZero() || duration.isNegative()) duration = Duration.ofSeconds(30);

        ackUntil.put(id, Instant.now().plus(duration));
    }

    public boolean isAcknowledged(PatientId id) {
        Instant until = ackUntil.get(id);
        if (until == null) return false;
        if (Instant.now().isAfter(until)) {
            ackUntil.remove(id);
            return false;
        }
        return true;
    }

    public void clear(PatientId id) {
        ackUntil.remove(id);
    }

    public void clearAll() {
        ackUntil.clear();
    }
}
