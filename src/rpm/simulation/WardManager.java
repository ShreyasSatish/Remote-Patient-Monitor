package rpm.simulation;

import rpm.domain.PatientId;
import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WardManager {
    public static final int MIN_PATIENTS = 8;

    private final Map<PatientId, PatientSimulator> patients = new LinkedHashMap<>();
    private final Map<PatientId, VitalSnapshot> latestSnapshots = new LinkedHashMap<>();
    private final Map<PatientId, double[]> lastEcgSegments = new LinkedHashMap<>();

    private final Random random = new Random();

    private int nextIdValue = 1;
    private PatientId selectedPatientId = new PatientId(1);
    private double secondsSinceLastVitals = 0.0;

    public WardManager(int initialPatients) {
        int n = Math.max(MIN_PATIENTS, initialPatients);
        Instant now = Instant.now();
        for (int i = 0; i < n; i++) {
            addPatientInternal(now);
        }
        selectedPatientId = new PatientId(1);
    }

    public synchronized List<PatientId> getPatientIds() {
        return new ArrayList<>(patients.keySet());
    }

    public synchronized int getPatientCount() {
        return patients.size();
    }

    public synchronized PatientId getSelectedPatientId() {
        return selectedPatientId;
    }

    public synchronized void setSelectedPatientId(PatientId id) {
        if (id == null || !patients.containsKey(id)) {
            selectedPatientId = new PatientId(1);
            return;
        }
        selectedPatientId = id;
    }

    public synchronized PatientId addPatient() {
        return addPatientInternal(Instant.now());
    }

    public synchronized boolean removePatient(PatientId id) {
        if (id == null || !patients.containsKey(id)) {
            return false;
        }
        if (id.getValue() <= MIN_PATIENTS) {
            return false;
        }
        if (patients.size() <= MIN_PATIENTS) {
            return false;
        }

        patients.remove(id);
        latestSnapshots.remove(id);
        lastEcgSegments.remove(id);

        if (id.equals(selectedPatientId)) {
            selectedPatientId = new PatientId(1);
        }
        return true;
    }

    public synchronized void tick(Instant time, double dtSeconds) {
        for (Map.Entry<PatientId, PatientSimulator> e : patients.entrySet()) {
            PatientSimulator sim = e.getValue();
            sim.advanceEcg(dtSeconds);
            lastEcgSegments.put(e.getKey(), sim.getLastEcgSegment());
        }

        secondsSinceLastVitals += dtSeconds;
        if (secondsSinceLastVitals >= 1.0) {
            secondsSinceLastVitals -= 1.0;
            for (Map.Entry<PatientId, PatientSimulator> entry : patients.entrySet()) {
                VitalSnapshot snap = entry.getValue().nextSnapshot(time);
                latestSnapshots.put(entry.getKey(), snap);
            }
        }
    }

    public synchronized List<PatientVitalsRow> getLatestVitalsTable() {
        List<PatientVitalsRow> rows = new ArrayList<>(patients.size());
        for (Map.Entry<PatientId, VitalSnapshot> entry : latestSnapshots.entrySet()) {
            PatientId id = entry.getKey();
            VitalSnapshot snap = entry.getValue();
            Map<VitalType, Double> values = snap.getValues();

            rows.add(new PatientVitalsRow(
                    id,
                    snap.getTimestamp(),
                    safeGet(values, VitalType.HEART_RATE),
                    safeGet(values, VitalType.RESP_RATE),
                    safeGet(values, VitalType.BP_SYSTOLIC),
                    safeGet(values, VitalType.BP_DIASTOLIC),
                    safeGet(values, VitalType.TEMPERATURE)
            ));
        }
        return rows;
    }

    public synchronized VitalSnapshot getPatientLatestSnapshot(PatientId id) {
        return latestSnapshots.get(id);
    }

    public synchronized VitalSnapshot getSelectedPatientLatestSnapshot() {
        return latestSnapshots.get(selectedPatientId);
    }

    public synchronized double[] getPatientLastEcgSegment(PatientId id) {
        double[] seg = lastEcgSegments.get(id);
        return seg != null ? seg : new double[0];
    }

    public synchronized double[] getSelectedPatientLastEcgSegment() {
        return getPatientLastEcgSegment(selectedPatientId);
    }

    private PatientId addPatientInternal(Instant now) {
        PatientId id = new PatientId(nextIdValue++);
        PatientScenario scenario = PatientScenario.NORMAL_ADULT;

        PatientSimulator simulator;
        if (id.getValue() <= MIN_PATIENTS) {
            simulator = PatientScenarioFactory.create(scenario);
        } else {
            PatientProfile profile = PatientProfile.generateNormal(scenario, random);
            simulator = PatientScenarioFactory.create(scenario, profile);
        }

        patients.put(id, simulator);
        latestSnapshots.put(id, simulator.nextSnapshot(now));
        lastEcgSegments.put(id, simulator.getLastEcgSegment());
        return id;
    }

    private static double safeGet(Map<VitalType, Double> values, VitalType type) {
        Double v = values.get(type);
        return v != null ? v : Double.NaN;
    }
}
