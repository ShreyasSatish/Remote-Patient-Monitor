package rpm.simulation;

/**
 * Factory for building a PatientSimulator configured for a given scenario.
 */
public final class PatientScenarioFactory {

    private PatientScenarioFactory() {
        // no instances
    }

    public static PatientSimulator create(PatientScenario scenario) {
        // Heart rate simulator (baseline / min / max)
        HeartRateSimulator hrSim = new HeartRateSimulator(
                scenario.getHrBaseline(),
                scenario.getHrMin(),
                scenario.getHrMax()
        );

        // Respiratory rate simulator
        RespRateSimulator rrSim = new RespRateSimulator(
                scenario.getRrBaseline(),
                scenario.getRrMin(),
                scenario.getRrMax()
        );

        // Blood pressure simulator
        BloodPressureSimulator bpSim = new BloodPressureSimulator(
                scenario.getBpBaselineSystolic(),
                scenario.getBpBaselineDiastolic(),
                scenario.getBpMinSystolic(),
                scenario.getBpMaxSystolic()
        );

        // Temperature simulator
        TemperatureSimulator tempSim = new TemperatureSimulator(
                scenario.getTempBaseline(),
                scenario.getTempMin(),
                scenario.getTempMax()
        );

        // Everything to the PatientSimulator constructor
        return new PatientSimulator(hrSim, rrSim, bpSim, tempSim);
    }
}
