package rpm.simulation;

import rpm.domain.VitalSnapshot;
import rpm.domain.VitalType;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

/**
 * Simulates a single patient by combining individual vital sign simulators.
 * Produces a VitalSnapshot for each requested time instant.
 */
public class PatientSimulator {

    private final HeartRateSimulator heartRateSimulator;
    private final RespRateSimulator respRateSimulator;
    private final BloodPressureSimulator bloodPressureSimulator;
    private final TemperatureSimulator temperatureSimulator;

    public PatientSimulator() {
        this.heartRateSimulator = new HeartRateSimulator();
        this.respRateSimulator = new RespRateSimulator();
        this.bloodPressureSimulator = new BloodPressureSimulator();
        this.temperatureSimulator = new TemperatureSimulator();
    }

    public VitalSnapshot nextSnapshot(Instant time) {
        Map<VitalType, Double> values = new EnumMap<>(VitalType.class);

        double heartRate = heartRateSimulator.nextValue(time);
        double respRate = respRateSimulator.nextValue(time);
        double systolic = bloodPressureSimulator.nextValue(time);
        double diastolic = bloodPressureSimulator.getCurrentDiastolic();
        double temperature = temperatureSimulator.nextValue(time);

        values.put(VitalType.HEART_RATE, heartRate);
        values.put(VitalType.RESP_RATE, respRate);
        values.put(VitalType.BP_SYSTOLIC, systolic);
        values.put(VitalType.BP_DIASTOLIC, diastolic);
        values.put(VitalType.TEMPERATURE, temperature);

        return new VitalSnapshot(time, values);
    }
}
