package rpm.domain;

import java.time.Instant;
import java.util.Map;



public class VitalSnapshot {

    private final Instant timestamp;
    private final Map<VitalType, Double> values;

    public VitalSnapshot(Instant timestamp, Map<VitalType, Double> values) {
        this.timestamp = timestamp;
        this.values = values;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<VitalType, Double> getValues() {
        return values;
    }
}
