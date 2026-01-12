package rpm.domain.report;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.alarm.AlarmListener;
import rpm.domain.alarm.AlarmTransition;
import rpm.simulation.PatientEvent;
import rpm.simulation.WardDataListener;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * NOTE - just for testing report now.
 * stores a rolling window of snapshots + alarm transitions in memory
 * will replace this with servlet history data once that is set up
 * - sb
 */
public final class InMemoryPatientDataStore implements PatientDataSource, WardDataListener, AlarmListener {

    private final Duration retention;
    private final Map<PatientId, Deque<VitalSnapshot>> vitalsByPatient = new HashMap<>();
    private final Map<PatientId, Deque<AlarmTransition>> alarmsByPatient = new HashMap<>();

    public InMemoryPatientDataStore(Duration retention) {
        this.retention = retention;
    }

    // WardDataListener
    @Override
    public synchronized void onVitalsSnapshot(PatientId id, VitalSnapshot snapshot) {
        Deque<VitalSnapshot> q = vitalsByPatient.computeIfAbsent(id, k -> new ArrayDeque<>());
        q.addLast(snapshot);
        pruneVitals(q, snapshot.getTimestamp());
    }

    @Override
    public void onEcgSegment(PatientId id, Instant time, double[] segment) {
        // not needed for 1 min vitals report
    }

    @Override
    public void onEventStarted(PatientId id, PatientEvent ev) {
        // optional: could store started/ended events if we want in rport too
    }

    @Override
    public void onEventEnded(PatientId id, PatientEvent ev) {
        // optional
    }

    // AlarmListener
    @Override
    public synchronized void onAlarmTransition(AlarmTransition transition) {
        PatientId id = transition.getPatientId();
        Deque<AlarmTransition> q = alarmsByPatient.computeIfAbsent(id, k -> new ArrayDeque<>());
        q.addLast(transition);
        pruneAlarms(q, transition.getTime());
    }

    @Override
    public void onAlarmState(PatientId id, Instant time, rpm.domain.alarm.AlarmState state) {
        // we only store transitions for reporting right now
    }

    // PatientDataSource
    @Override
    public synchronized List<VitalSnapshot> getVitals(PatientId id, Instant from, Instant to) {
        Deque<VitalSnapshot> q = vitalsByPatient.get(id);
        if (q == null) return Collections.emptyList();
        return q.stream()
                .filter(s -> !s.getTimestamp().isBefore(from) && !s.getTimestamp().isAfter(to))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<AlarmTransition> getAlarmTransitions(PatientId id, Instant from, Instant to) {
        Deque<AlarmTransition> q = alarmsByPatient.get(id);
        if (q == null) return Collections.emptyList();
        return q.stream()
                .filter(a -> !a.getTime().isBefore(from) && !a.getTime().isAfter(to))
                .collect(Collectors.toList());
    }

    private void pruneVitals(Deque<VitalSnapshot> q, Instant now) {
        Instant cutoff = now.minus(retention);
        while (!q.isEmpty() && q.peekFirst().getTimestamp().isBefore(cutoff)) {
            q.removeFirst();
        }
    }

    private void pruneAlarms(Deque<AlarmTransition> q, Instant now) {
        Instant cutoff = now.minus(retention);
        while (!q.isEmpty() && q.peekFirst().getTime().isBefore(cutoff)) {
            q.removeFirst();
        }
    }
}
