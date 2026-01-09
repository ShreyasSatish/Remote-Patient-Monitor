package rpm.domain.alarm;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;

import java.time.Instant;
import java.util.*;

public final class AlarmEngine {

    private final AlarmConfig config;

    private final Map<PatientId, EnumMap<VitalType, VitalTracker>> trackers = new HashMap<>();
    private final Map<PatientId, AlarmState> lastState = new HashMap<>();

    public AlarmEngine(AlarmConfig config) {
        this.config = config;
    }

    public AlarmState getState(PatientId id) {
        return lastState.get(id);
    }

    public List<AlarmTransition> update(PatientId id, VitalSnapshot snapshot) {
        EnumMap<VitalType, VitalTracker> perVital =
                trackers.computeIfAbsent(id, k -> new EnumMap<>(VitalType.class));

        Instant t = snapshot.getTimestamp();
        Map<VitalType, Double> values = snapshot.getValues();

        List<AlarmTransition> transitions = new ArrayList<>();
        EnumMap<VitalType, VitalAlarmStatus> statusMap = new EnumMap<>(VitalType.class);

        AlarmLevel overall = AlarmLevel.GREEN;

        for (VitalType vt : VitalType.values()) {
            ThresholdBand band = config.band(vt);
            if (band == null) continue; // no thresholds set for this signal

            double v = values.getOrDefault(vt, Double.NaN);
            if (Double.isNaN(v) || Double.isInfinite(v)) continue;

            VitalTracker tracker = perVital.computeIfAbsent(vt, k -> new VitalTracker(vt));

            AlarmLevel old = tracker.level;
            tracker.step(v, band, config, t);

            if (tracker.level != old) {
                transitions.add(new AlarmTransition(id, vt, old, tracker.level, t, tracker.reason));
            }

            if (tracker.level != AlarmLevel.GREEN) {
                statusMap.put(vt, new VitalAlarmStatus(vt, tracker.level, tracker.reason, tracker.since));
            }

            overall = AlarmLevel.max(overall, tracker.level);
        }

        AlarmState newState = new AlarmState(overall, statusMap);
        lastState.put(id, newState);
        return transitions;
    }

    // internal per-vital tracker
    private static final class VitalTracker {
        final VitalType type;

        AlarmLevel level = AlarmLevel.GREEN;
        int amberCount = 0;
        int redCount = 0;

        Instant since = null;
        String reason = "";

        VitalTracker(VitalType type) { this.type = type; }

        void step(double value, ThresholdBand b, AlarmConfig cfg, Instant t) {
            AlarmLevel instantaneous = classify(value, b);

            // persistence counters
            if (instantaneous == AlarmLevel.RED) {
                redCount++;
            } else {
                redCount = 0;
            }
            if (instantaneous == AlarmLevel.AMBER) {
                amberCount++;
            } else {
                amberCount = 0;
            }

            AlarmLevel next = level;

            // escalate
            if (level != AlarmLevel.RED && redCount >= cfg.redPersistSeconds) next = AlarmLevel.RED;
            else if (level == AlarmLevel.GREEN && amberCount >= cfg.amberPersistSeconds) next = AlarmLevel.AMBER;

            // de-escalate with hysteresis: require it to be safe enough
            if (level == AlarmLevel.RED && instantaneous != AlarmLevel.RED && isClearlyNotRed(value, b, cfg.hysteresis)) {
                next = AlarmLevel.AMBER; // step down gradually
            }
            if (level == AlarmLevel.AMBER && instantaneous == AlarmLevel.GREEN && isClearlyGreen(value, b, cfg.hysteresis)) {
                next = AlarmLevel.GREEN;
            }

            if (next != level) {
                level = next;
                since = t;
            }

            reason = buildReason(value, b, level);
            if (level == AlarmLevel.GREEN) {
                since = null;
                reason = "";
            }
        }

        private static AlarmLevel classify(double v, ThresholdBand b) {
            if (v <= b.lowRed || v >= b.highRed) return AlarmLevel.RED;
            if (v <= b.lowAmber || v >= b.highAmber) return AlarmLevel.AMBER;
            return AlarmLevel.GREEN;
        }

        private static boolean isClearlyGreen(double v, ThresholdBand b, double h) {
            return (v > b.lowAmber + h) && (v < b.highAmber - h);
        }

        private static boolean isClearlyNotRed(double v, ThresholdBand b, double h) {
            return (v > b.lowRed + h) && (v < b.highRed - h);
        }

        private static String buildReason(double v, ThresholdBand b, AlarmLevel level) {
            if (level == AlarmLevel.GREEN) return "";
            boolean low = v <= b.lowAmber;
            boolean high = v >= b.highAmber;

            if (level == AlarmLevel.RED) {
                if (v <= b.lowRed) return "Critically low";
                if (v >= b.highRed) return "Critically high";
            }
            if (low) return "Low";
            if (high) return "High";
            return "Abnormal";
        }
    }
}
